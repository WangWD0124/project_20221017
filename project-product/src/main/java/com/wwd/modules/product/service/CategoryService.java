package com.wwd.modules.product.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.CategoryDTO;
import com.wwd.modules.product.entity.CategoryEntity;
import com.wwd.modules.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

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

    List<CategoryDTO> getCategoryLevel1();

    Map<String, List<Catelog2Vo>> getCatelogJson() throws InterruptedException;
}