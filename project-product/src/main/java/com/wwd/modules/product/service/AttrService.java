package com.wwd.modules.product.service;

import com.wwd.common.page.PageData;
import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.AttrDTO;
import com.wwd.modules.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface AttrService extends CrudService<AttrEntity, AttrDTO> {

    PageData<AttrDTO> page(Map<String, Object> params, String attrType, Long catelog_id);
}