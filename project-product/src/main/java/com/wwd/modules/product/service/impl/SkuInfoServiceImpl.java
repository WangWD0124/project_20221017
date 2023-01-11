package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.page.PageData;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.modules.product.dao.*;
import com.wwd.modules.product.dto.SkuImagesDTO;
import com.wwd.modules.product.dto.SkuInfoDTO;
import com.wwd.modules.product.dto.SpuInfoDTO;
import com.wwd.modules.product.entity.SkuImagesEntity;
import com.wwd.modules.product.entity.SkuInfoEntity;
import com.wwd.modules.product.entity.SkuSaleAttrValueEntity;
import com.wwd.modules.product.entity.SpuInfoEntity;
import com.wwd.modules.product.service.AttrGroupService;
import com.wwd.modules.product.service.ProductAttrValueService;
import com.wwd.modules.product.service.SkuInfoService;
import com.wwd.modules.product.service.SkuSaleAttrValueService;
import com.wwd.modules.product.vo.SkuItemVo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * sku信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class SkuInfoServiceImpl extends CrudServiceImpl<SkuInfoDao, SkuInfoEntity, SkuInfoDTO> implements SkuInfoService {

    @Autowired
    private SkuImagesDao skuImagesDao;
    @Autowired
    private SpuInfoDao spuInfoDao;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ThreadPoolExecutor executor;

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

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {

        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SkuInfoEntity::getSpuId, spuId);
        return baseDao.selectList(wrapper);
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {

        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoDTO> infoFuture = CompletableFuture.supplyAsync(() -> {
            //sku基本信息
            SkuInfoDTO skuInfoDTO = ConvertUtils.sourceToTarget(baseDao.selectById(skuId), SkuInfoDTO.class);
            skuItemVo.setInfo(skuInfoDTO);
            return skuInfoDTO;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //sku销售属性
            List<SkuItemVo.SkuItemSaleAttrVo> skuSaleAttrVoList = skuSaleAttrValueService.getSkuSaleAttrVoBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(skuSaleAttrVoList);
        }, executor);


        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //spu描述
            SpuInfoDTO spuInfoDTO = ConvertUtils.sourceToTarget(spuInfoDao.selectById(res.getSpuId()), SpuInfoDTO.class);
            skuItemVo.setDesc(spuInfoDTO);
        }, executor);

        CompletableFuture<Void> groupAttrsFuture = infoFuture.thenAcceptAsync((res) -> {
            //spu规格属性
            List<SkuItemVo.SpuItemBaseAttrVo> spuBaseAttrGroupVoList = attrGroupService.getGroupWithBaseAttrBySpuId(res.getSpuId());
            skuItemVo.setGroupAttrs(spuBaseAttrGroupVoList);
        }, executor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //sku图片集
            LambdaQueryWrapper<SkuImagesEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SkuImagesEntity::getSkuId, skuId);
            List<SkuImagesDTO> skuImagesDTOList = ConvertUtils.sourceToTarget(skuImagesDao.selectList(wrapper), SkuImagesDTO.class);
            skuItemVo.setImages(skuImagesDTOList);
        }, executor);

        CompletableFuture.allOf(saleAttrFuture, descFuture, groupAttrsFuture, imagesFuture).get();

        return skuItemVo;
    }

}