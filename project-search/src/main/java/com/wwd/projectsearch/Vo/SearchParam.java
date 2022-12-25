package com.wwd.projectsearch.Vo;

import lombok.Data;

import java.util.List;


/**
 * 封装查询条件
 */
@Data
public class SearchParam {

    private String keyword;//关键字
    private Long categoryLevel3Id;//三级分类id
    private String sort;//排序
    private Integer hasStock;//是否有库存
    private String skuPrice;//价格区间
    private List<Long> brandIds;//品牌id
    private List<String> attrs;//属性及其值
    private Integer pageNum;//页码
 }
