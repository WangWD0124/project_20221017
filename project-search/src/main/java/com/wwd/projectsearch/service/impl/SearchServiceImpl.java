package com.wwd.projectsearch.service.impl;

import com.wwd.projectsearch.Vo.SearchParam;
import com.wwd.projectsearch.Vo.SearchResult;
import com.wwd.projectsearch.config.ElasticSearchConfig;
import com.wwd.projectsearch.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchResult result = null;

        //构建检索请求
        SearchRequest searchRequest = buildSearchRequest();
        try {
            //执行检索请求
            SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private SearchRequest buildSearchRequest(SearchParam param) {

        //构建DSL语句
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(param.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        if (param.getCategoryLevel3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryLevel3Id", param.getCategoryLevel3Id()));
        }
        if (param.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock()));
        }
        if (param.getSort())
        return
    }
}
