package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.page.PageData;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.product.dao.SkuInfoDao;
import com.wwd.modules.product.dto.SkuInfoDTO;
import com.wwd.modules.product.entity.SkuInfoEntity;
import com.wwd.modules.product.entity.SpuInfoEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * sku信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class SkuInfoServiceImpl extends CrudServiceImpl<SkuInfoDao, SkuInfoEntity, SkuInfoDTO> implements SkuInfoService {

    @Override
    public QueryWrapper<SkuInfoEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public PageData<SkuInfoDTO> search(Map<String, Object> params) {

        String key = (String) params.get("key");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");

        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();

        if (!catelogId.equals("0")){
            wrapper.eq(SkuInfoEntity::getCatalogId, catelogId);
        }
        if (!brandId.equals("0")){
            wrapper.eq(SkuInfoEntity::getBrandId, brandId);
        }
        if (!min.equals("0")){
            wrapper.gt(SkuInfoEntity::getPrice, min);
        }
        if (!max.equals("0")){
            wrapper.lt(SkuInfoEntity::getPrice, max);
        }
        if (key != null && !key.equals("")){
            wrapper.and(i -> i.eq(SkuInfoEntity::getSkuId, key)
                    .or().like(SkuInfoEntity::getSkuName, key));
        }
        IPage<SkuInfoEntity> page = baseDao.selectPage(getPage(params, null, true), wrapper);
        return getPageData(page, currentDtoClass());
    }
}