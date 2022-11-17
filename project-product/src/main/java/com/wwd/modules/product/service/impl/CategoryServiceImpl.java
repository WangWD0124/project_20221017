package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.modules.product.dao.CategoryDao;
import com.wwd.modules.product.dto.CategoryDTO;
import com.wwd.modules.product.entity.CategoryEntity;
import com.wwd.modules.product.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品三级分类
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class CategoryServiceImpl extends CrudServiceImpl<CategoryDao, CategoryEntity, CategoryDTO> implements CategoryService {

    @Override
    public QueryWrapper<CategoryEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    /**
     * 查询全部三级分类，并以树形结构返回
     */
    @Override
    public List<CategoryDTO> listWithTree() {
        List<CategoryEntity> allCategoryEntities = baseDao.selectList(null);//全部分类
        return allCategoryEntities.stream().filter(categoryEntity ->
                categoryEntity.getCatLevel() == 1 //全部一级分类
        ).map(rootCategoryEntity -> {
            CategoryDTO rootCategoryDTO = ConvertUtils.sourceToTarget(rootCategoryEntity, CategoryDTO.class);
            rootCategoryDTO.setChildren(getChildrens(rootCategoryEntity, allCategoryEntities));//为一级分类添加二级分类
            return rootCategoryDTO;
        }).collect(Collectors.toList());

    }

    private List<CategoryDTO> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream().filter(categoryEntity ->
                categoryEntity.getParentCid().equals(root.getCatId())
        ).map(rootCategoryEntity -> {
            CategoryDTO rootCategoryDTO = ConvertUtils.sourceToTarget(rootCategoryEntity, CategoryDTO.class);
            rootCategoryDTO.setChildren(getChildrens(rootCategoryEntity, all));//为上一级分类添加下一级分类
            return rootCategoryDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> catelogPath = new ArrayList<>();
        catelogPath.add(catelogId);
        findParentCid(catelogPath, catelogId);
        Collections.reverse(catelogPath);
        return catelogPath.toArray(new Long[catelogPath.size()]);
    }

    private void findParentCid(List<Long> catelogPath, Long catelogId) {
        Long parentCid = baseDao.getParentCidBycatId(catelogId);
        if (parentCid != 0){
            catelogPath.add(parentCid);
            findParentCid(catelogPath, parentCid);
        }
    }

    @Override
    public String findCategoryName(Long catelogId) {
        return baseDao.getCategoryNameBycatId(catelogId);
    }
}