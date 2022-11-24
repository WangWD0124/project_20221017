package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwd.common.page.PageData;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.product.dao.SpuInfoDao;
import com.wwd.modules.product.dto.SpuInfoDTO;
import com.wwd.modules.product.entity.SpuInfoEntity;
import com.wwd.modules.product.service.SpuInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * spu信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class SpuInfoServiceImpl extends CrudServiceImpl<SpuInfoDao, SpuInfoEntity, SpuInfoDTO> implements SpuInfoService {

    @Override
    public QueryWrapper<SpuInfoEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public PageData<SpuInfoDTO> search(Map<String, Object> params) {

        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");

        LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();

        if (catelogId != null){
            wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }
        if (brandId != null){
            wrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }
        if (status != null){
            wrapper.eq(SpuInfoEntity::getPublishStatus, params.get("status"));
        }
        if (key != null){
            wrapper.eq(SpuInfoEntity::getId, key)
                    .or().like(SpuInfoEntity::getSpuName, key)
                    .or().like(SpuInfoEntity::getSpuDescription, key);
        }
        IPage<SpuInfoEntity> page = baseDao.selectPage(getPage(params, null, true), wrapper);
        return getPageData(page, currentDtoClass());
    }
}