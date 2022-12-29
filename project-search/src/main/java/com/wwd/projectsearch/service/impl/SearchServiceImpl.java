package com.wwd.projectsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.wwd.common.es.SkuEsModel;
import com.wwd.projectsearch.Vo.SearchParam;
import com.wwd.projectsearch.Vo.SearchResult;
import com.wwd.projectsearch.config.ElasticSearchConfig;
import com.wwd.projectsearch.constant.EsConstant;
import com.wwd.projectsearch.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam searchParam) {

        SearchResult result = null;

        //构建检索请求
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        try {
            //执行检索请求
            SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

            //封装响应数据
            result = buildSearchResult(searchParam, searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private SearchRequest buildSearchRequest(SearchParam param) {

        //构建DSL语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //按关键字
        if (!StringUtils.isEmpty(param.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        //按分类ID
        if (param.getCategoryLevel3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCategoryLevel3Id()));
        }
        //按品牌ID
        if (param.getBrandIds()!=null && param.getBrandIds().size()>0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandIds()));
        }
        //按属性
        if (param.getAttrs()!=null && param.getAttrs().size()>0){

            for (String attr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValues", attrValues));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", null, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        //按是否有库存
        if (param.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock()==1));
        }
        searchSourceBuilder.query(boolQueryBuilder);

        //按价格区间
        if (!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
            String[] s = param.getSkuPrice().split("_");
            if (s.length==2){
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            } else if (param.getSkuPrice().startsWith("_")){
                rangeQueryBuilder.lte(s[0]);
            } else if (param.getSkuPrice().endsWith("_")){
                rangeQueryBuilder.gte(s[0]);
            }
            searchSourceBuilder.query(rangeQueryBuilder);
        }


        //按排序
        if (!StringUtils.isEmpty(param.getSort())){
            String[] s = param.getSort().split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            searchSourceBuilder.sort(s[0], sortOrder);
        }
        //按页码
        searchSourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        //按高亮
        if (!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合查询
         */
        //聚合查询品牌（包括名称、logo）
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("logo").size(1));
        searchSourceBuilder.aggregation(brand_agg);

        //聚合查询三级分类（包括名称）
        TermsAggregationBuilder category_agg = AggregationBuilders.terms("category_agg").field("catalogId").size(50);
        category_agg.subAggregation(AggregationBuilders.terms("category_name_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(category_agg);

        //聚合查询属性（包括名称、值列表）
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(1);
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(attr_agg);


        System.out.println("----->"+searchSourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        return searchRequest;
    }

    private SearchResult buildSearchResult(SearchParam searchParam, SearchResponse searchResponse) {

        SearchResult result = new SearchResult();

        //商品sku聚合查询
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        SearchHits hits = searchResponse.getHits();
        if (hits.getHits()!=null&&hits.getHits().length>0){
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                skuEsModel.setSkuTitle(skuTitle.fragments()[0].string());
                skuEsModels.add(skuEsModel);
            }
        }
        result.setProducts(skuEsModels);

        //三级分类聚合查询
        List<SearchResult.CategoryVo> categoryVos = new ArrayList<>();
        ParsedLongTerms category_agg = searchResponse.getAggregations().get("category_agg");
        List<? extends Terms.Bucket> category_aggBuckets = category_agg.getBuckets();
        for (Terms.Bucket category_aggBucket : category_aggBuckets) {
            SearchResult.CategoryVo categoryVo = new SearchResult.CategoryVo();
            long category_idKey = category_aggBucket.getKeyAsNumber().longValue();
            categoryVo.setCatelogId(category_idKey);

            ParsedStringTerms category_name_agg = category_aggBucket.getAggregations().get("category_name_agg");
            String category_nameKey = category_name_agg.getBuckets().get(0).getKeyAsString();
            categoryVo.setCatelogName(category_nameKey);

            categoryVos.add(categoryVo);
        }
        result.setCategoryVos(categoryVos);

        //品牌聚合查询
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        List<? extends Terms.Bucket> brand_aggBuckets = brand_agg.getBuckets();
        for (Terms.Bucket brand_aggBucket : brand_aggBuckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            long brand_idKey = brand_aggBucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brand_idKey);

            ParsedStringTerms brand_name_agg = brand_aggBucket.getAggregations().get("brand_name_agg");
            String brand_nameKey = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_nameKey);

            ParsedStringTerms brand_img_agg = brand_aggBucket.getAggregations().get("brand_img_agg");
            String brand_imgKey = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setLogo(brand_imgKey);

            brandVos.add(brandVo);
        }
        result.setBrandVos(brandVos);

        //属性及其值聚合查询
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> buckets = attr_id_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();

            long attr_idKey = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attr_idKey);

            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attr_nameKey = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_nameKey);

            List<String> attrValue = new ArrayList<>();
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<? extends Terms.Bucket> attr_value_aggBuckets = attr_value_agg.getBuckets();
            for (Terms.Bucket attr_value_aggBucket : attr_value_aggBuckets) {
                String attr_value_aggBucketKeyAsString = attr_value_aggBucket.getKeyAsString();
                attrValue.add(attr_value_aggBucketKeyAsString);
            }
            attrVo.setAttrValue(attrValue);
            attrVos.add(attrVo);
        }
        result.setAttrVos(attrVos);

        long total = searchResponse.getHits().getTotalHits().value;
        result.setTotal(total);

        result.setTotalPapes((int) (total/EsConstant.PRODUCT_PAGESIZE)+(total%EsConstant.PRODUCT_PAGESIZE==0?0:1));

        result.setPageNum(searchParam.getPageNum());

        return result;
    }
}
