package com.wwd.modules.product.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.CategoryDTO;
import com.wwd.modules.product.entity.CategoryEntity;

import java.util.List;

/**
 * 商品三级分类
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface CategoryService extends CrudService<CategoryEntity, CategoryDTO> {

    List<CategoryDTO> listWithTree();

    Long[] findCatelogPath(Long catelogId);

    String findCategoryName(Long catelogId);
}