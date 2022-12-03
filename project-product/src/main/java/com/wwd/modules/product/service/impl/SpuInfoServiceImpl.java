package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.es.SkuEsModel;
import com.wwd.common.feign.dto.ware.SkuHasStockVo;
import com.wwd.common.page.PageData;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.Result;
import com.wwd.modules.product.dao.SpuInfoDao;
import com.wwd.modules.product.dto.ProductAttrValueDTO;
import com.wwd.modules.product.dto.SpuInfoDTO;
import com.wwd.modules.product.entity.BrandEntity;
import com.wwd.modules.product.entity.ProductAttrValueEntity;
import com.wwd.modules.product.entity.SkuInfoEntity;
import com.wwd.modules.product.entity.SpuInfoEntity;
import com.wwd.modules.product.feign.SearchFeignService;
import com.wwd.modules.product.feign.WareFeignService;
import com.wwd.modules.product.service.*;
import com.wwd.modules.product.service.SkuInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * spu信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class SpuInfoServiceImpl extends CrudServiceImpl<SpuInfoDao, SpuInfoEntity, SpuInfoDTO> implements SpuInfoService {

    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

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

        if (!catelogId.equals("0")){
            wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }
        if (!brandId.equals("0")){
            wrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }
        if (status != null && !status.equals("")){
            wrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }
        if (key != null && !key.equals("")){
            wrapper.and(i -> i.eq(SpuInfoEntity::getId, key)
                    .or().like(SpuInfoEntity::getSpuName, key)
                    .or().like(SpuInfoEntity::getSpuDescription, key));
        }
        IPage<SpuInfoEntity> page = baseDao.selectPage(getPage(params, null, true), wrapper);
        return getPageData(page, currentDtoClass());
    }

    @Override
    public void updatePublishStatusById(Long id) {

        //一、组装传递给ES的数据
        //1、sku对应的spu的基本属性及其值的列表
        List<ProductAttrValueDTO> productAttrValueDTOS = productAttrValueService.getBaseAttrEnableSearchBySpuId(id);
        List<SkuEsModel.Attr> attrs = productAttrValueDTOS.stream().map(productAttrValueDTO -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            attr.setAttrId(productAttrValueDTO.getAttrId());
            attr.setAttrName(productAttrValueDTO.getAttrName());
            attr.setAttrValue(productAttrValueDTO.getAttrValue());
            return attr;
        }).collect(Collectors.toList());


        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuBySpuId(id);
        //2、sku的库存查询（一次性将全部skuId传递给ware远程查询）
        List<Long> skuIds = skuInfoEntities.stream().map(skuInfoEntity -> {
            return skuInfoEntity.getSkuId();
        }).collect(Collectors.toList());
        Map<Long, Long> map = null;
        try{
            List<SkuHasStockVo> data = wareFeignService.getSkuHasStockVoByIds(skuIds).getData();
            map = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getStock));
        } catch (Exception e){
            log.error("库存查询异常：原因{}", e);
        }

        // 3、sku信息，及相关信息
        Map<Long, Long> finalMap = map;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(skuInfoEntity -> {

            SkuEsModel skuEsModel = new SkuEsModel();
            skuIds.add(skuInfoEntity.getSkuId());
            BeanUtils.copyProperties(skuInfoEntity, skuEsModel);//同名属性值拷贝
            if (finalMap != null){
                skuEsModel.setHasStock(finalMap.get(skuInfoEntity.getSkuId()));//库存
            } else {
                skuEsModel.setHasStock(0L);//默认没库存
            }
            skuEsModel.setHotScore(10000L);//TODO//热度评分
            skuEsModel.setCatalogName(categoryService.findCategoryName(skuInfoEntity.getCatalogId()));
            BrandEntity brandEntity = brandService.selectById(skuInfoEntity.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setLogo(brandEntity.getLogo());
            skuEsModel.setAttrs(attrs);
            return skuEsModel;
        }).collect(Collectors.toList());

        //二、将组装好的数据传递给ES
        Result R = searchFeignService.productStatusUp(skuEsModels);
        if (R.getCode() == 0){
            //三、修改spu上架状态
            baseDao.updatePublishStatusById(id);
        } else {
            //TODO 执行失败，重复调用？接口幂等性，重试机制？
        }






    }
}