package com.wwd.modules.product.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.AttrAttrgroupRelationDTO;
import com.wwd.modules.product.entity.AttrAttrgroupRelationEntity;

/**
 * 属性&属性分组关联
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface AttrAttrgroupRelationService extends CrudService<AttrAttrgroupRelationEntity, AttrAttrgroupRelationDTO> {

    Long findAttrGroupId(Long attrId);

    AttrAttrgroupRelationDTO getByAttrId(Long attrId);
}