package com.wwd.modules.ware.service;

import com.wwd.common.feign.dto.ware.SkuHasStockVo;
import com.wwd.common.service.CrudService;
import com.wwd.modules.ware.dto.WareSkuDTO;
import com.wwd.modules.ware.entity.WareSkuEntity;

import java.util.List;

/**
 * 商品库存
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface WareSkuService extends CrudService<WareSkuEntity, WareSkuDTO> {

    WareSkuDTO getStockById(Long id);

    List<SkuHasStockVo> getSkuHasStockVoByIds(List<Long> skuIds);
}