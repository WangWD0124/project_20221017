package com.wwd.modules.product.service;

import com.wwd.common.page.PageData;
import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.AttrGroupDTO;
import com.wwd.modules.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface AttrGroupService extends CrudService<AttrGroupEntity, AttrGroupDTO> {

    PageData<AttrGroupDTO> page(Map<String, Object> params, Long catelog_id);

    List<AttrGroupDTO> listWithAttrByCatelog_id(Long catelog_id);
}