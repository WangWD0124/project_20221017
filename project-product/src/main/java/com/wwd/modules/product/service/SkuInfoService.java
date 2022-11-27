package com.wwd.modules.product.service;

import com.wwd.common.page.PageData;
import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.SkuInfoDTO;
import com.wwd.modules.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface SkuInfoService extends CrudService<SkuInfoEntity, SkuInfoDTO> {

    PageData<SkuInfoDTO> search(Map<String, Object> params);
}