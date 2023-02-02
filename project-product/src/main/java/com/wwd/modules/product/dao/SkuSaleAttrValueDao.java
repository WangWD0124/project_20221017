package com.wwd.modules.product.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.product.entity.SkuSaleAttrValueEntity;
import com.wwd.modules.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseDao<SkuSaleAttrValueEntity> {

    List<SkuItemVo.SkuItemSaleAttrVo> getSkuSaleAttrVoBySpuId(@Param("spu_id") Long spu_id);

    List<String> getSaleAttrValueStringListBySkuId(@Param("sku_id") Long sku_id);
}