package com.wwd.modules.ware.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * εεεΊε­
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface WareSkuDao extends BaseDao<WareSkuEntity> {

    Long getSkuHasStockVoBySkuId(Long sku_id);
	
}