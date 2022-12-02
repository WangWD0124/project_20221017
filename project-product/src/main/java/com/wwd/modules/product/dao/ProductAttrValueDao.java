package com.wwd.modules.product.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.product.entity.ProductAttrValueEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface ProductAttrValueDao extends BaseDao<ProductAttrValueEntity> {

    void updateValue(@Param("attr_value") String attrValue, @Param("quick_show") Integer quickShow, @Param("spu_id") Long spuId, @Param("attr_id") Long attrId);

    List<ProductAttrValueEntity> getBaseAttrEnableSearchBySpuId(@Param("spu_id") Long spu_id);
}