package com.wwd.modules.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.feign.dto.ware.SkuHasStockVo;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.ware.dao.WareSkuDao;
import com.wwd.modules.ware.dto.WareSkuDTO;
import com.wwd.modules.ware.entity.WareSkuEntity;
import com.wwd.modules.ware.service.WareSkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品库存
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class WareSkuServiceImpl extends CrudServiceImpl<WareSkuDao, WareSkuEntity, WareSkuDTO> implements WareSkuService {

    @Override
    public QueryWrapper<WareSkuEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public WareSkuDTO getStockById(Long id) {

        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<WareSkuEntity>();
        wrapper.eq(WareSkuEntity::getId, id);
        return null;
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStockVoByIds(List<Long> skuIds) {

        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(skuId);
            Integer stock = baseDao.getSkuHasStockVoBySkuId(skuId);
            skuHasStockVo.setStock(stock);
            return skuHasStockVo;
        }).collect(Collectors.toList());

        return skuHasStockVos;
    }
}