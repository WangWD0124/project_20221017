package com.wwd.modules.product.service;

import com.wwd.common.page.PageData;
import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.CategoryBrandRelationDTO;
import com.wwd.modules.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface CategoryBrandRelationService extends CrudService<CategoryBrandRelationEntity, CategoryBrandRelationDTO> {

    PageData<CategoryBrandRelationDTO> page(Map<String, Object> params, Long brandId);

    List<CategoryBrandRelationDTO> getByCatelog_id(Long catelog_id);
}