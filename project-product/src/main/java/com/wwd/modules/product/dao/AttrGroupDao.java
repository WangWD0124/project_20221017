package com.wwd.modules.product.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.product.entity.AttrGroupEntity;
import com.wwd.modules.product.entity.ProductAttrValueEntity;
import com.wwd.modules.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface AttrGroupDao extends BaseDao<AttrGroupEntity> {

    List<ProductAttrValueEntity> getBaseAttrEnableSearchBySpuId(@Param("spu_id") Long spu_id);

    List<SkuItemVo.SpuItemBaseAttrVo> getGroupWithBaseAttrBySpuId(@Param("spu_id") Long spu_id);
}