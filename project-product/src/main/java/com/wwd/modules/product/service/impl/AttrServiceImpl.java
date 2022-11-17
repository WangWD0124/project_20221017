package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.page.PageData;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.product.dao.AttrDao;
import com.wwd.modules.product.dto.AttrDTO;
import com.wwd.modules.product.entity.AttrEntity;
import com.wwd.modules.product.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商品属性
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class AttrServiceImpl extends CrudServiceImpl<AttrDao, AttrEntity, AttrDTO> implements AttrService {

    @Override
    public QueryWrapper<AttrEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public PageData<AttrDTO> page(Map<String, Object> params, String attrType, Long catelog_id) {

        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper();
        wrapper.eq(AttrEntity::getAttrType, attrType);//查询规格属性或销售属性
        if (catelog_id != 0){
            wrapper.eq(AttrEntity::getCatelogId, catelog_id);
        }
        String key = (String) params.get("key");
        if (key != null){
            wrapper.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
        }
        IPage<AttrEntity> page = baseDao.selectPage(getPage(params, null, true), wrapper);
        return getPageData(page, currentDtoClass());
    }
}