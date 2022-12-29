package com.wwd.projectsearch.Vo;


import com.wwd.common.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
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
        private Long catelogId;
        private String catelogName;
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

    // =====以上是要返回给页面的所有信息

    //面包屑导航
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    /**
     * 面包屑导航Vo
     */
    @Data
    public static class NavVo {
        private String navName;   //导航名称
        private String navValue; //导航值
        private String link;  //导航跳转连接
    }






}
