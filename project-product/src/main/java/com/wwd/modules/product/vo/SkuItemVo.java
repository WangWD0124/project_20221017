package com.wwd.modules.product.vo;


import com.wwd.modules.product.dto.SkuImagesDTO;
import com.wwd.modules.product.dto.SkuInfoDTO;
import com.wwd.modules.product.dto.SkuSaleAttrValueDTO;
import com.wwd.modules.product.dto.SpuInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    //sku基本信息
    private SkuInfoDTO info;

    private boolean hasStock = true;

    //sku图片集
    private List<SkuImagesDTO> images;

    //sku销售属性
    private List<SkuItemSaleAttrVo> saleAttr;//相关属性及其系列值
    @Data
    public static class SkuItemSaleAttrVo{
        //private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;
    }
    @Data
    public static class AttrValueWithSkuIdVo{
        private String attrValue;
        private String skuIds;
    }

    //spu描述
    private SpuInfoDTO desc;

    //spu规格属性
    private List<SpuItemBaseAttrVo> groupAttrs;//相关属性及其系列值
    @Data
    public static class SpuItemBaseAttrVo{
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }
    @Data
    public static class SpuBaseAttrVo{
        //private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
