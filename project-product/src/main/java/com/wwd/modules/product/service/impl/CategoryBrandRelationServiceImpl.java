package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.page.PageData;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.modules.product.dao.CategoryBrandRelationDao;
import com.wwd.modules.product.dto.CategoryBrandRelationDTO;
import com.wwd.modules.product.entity.CategoryBrandRelationEntity;
import com.wwd.modules.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class CategoryBrandRelationServiceImpl extends CrudServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity, CategoryBrandRelationDTO> implements CategoryBrandRelationService {

    @Override
    public QueryWrapper<CategoryBrandRelationEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public PageData<CategoryBrandRelationDTO> page(Map<String, Object> params, Long brandId) {

        LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper();
        wrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);
        IPage<CategoryBrandRelationEntity> page = baseDao.selectPage(getPage(params, null, true), wrapper);
        return getPageData(page, currentDtoClass());
    }

    @Override
    public List<CategoryBrandRelationDTO> getByCatelog_id(Long catelog_id) {

        LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper();
        wrapper.eq(CategoryBrandRelationEntity::getCatelogId, catelog_id);
        List<CategoryBrandRelationEntity> list = baseDao.selectList(wrapper);
        return ConvertUtils.sourceToTarget(list, CategoryBrandRelationDTO.class);
    }

}