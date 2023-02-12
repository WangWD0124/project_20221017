package com.wwd.modules.ware.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface WareSkuDao extends BaseDao<WareSkuEntity> {

    Long getSkuHasStockVoBySkuId(Long sku_id);

    List<Long> getWareIdsHasStickBySkuId(Long sku_id);

    Long orderLockStock(Long wareId, Long skuId, Integer num);

    Long orderReleaseStockWithNo(@Param("wareId") Long ware_id, @Param("skuId") Long sku_id, @Param("num") Integer num, @Param("taskId") Long task_id);

    Long orderReleaseStockWithOk(@Param("wareId") Long ware_id, @Param("skuId") Long sku_id, @Param("num") Integer num, @Param("taskId") Long task_id);

}