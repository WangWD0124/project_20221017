package com.wwd.modules.product.service;

import com.wwd.common.page.PageData;
import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.BrandDTO;
import com.wwd.modules.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface BrandService extends CrudService<BrandEntity, BrandDTO> {

    PageData<BrandDTO> pageByCondition(Map<String, Object> params);

}