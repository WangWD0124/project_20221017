package com.wwd.projectsearch.Vo;


import com.wwd.common.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * 封装查询返回结果
 */
@Data
public class SearchResult {

    private List<SkuEsModel> products;

    private Integer pageNum;

    private Long total;

    private Integer totalPapes;

    private List<CategoryVo> categoryVos;//相关分类
    @Data
    public static class CategoryVo{
        private Long catalogId;
        private String catalogName;
    }

    private List<BrandVo> brandVos;//相关品牌
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String logo;
    }

    private List<AttrVo> attrVos;//相关属性及其系列值
    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }






}
