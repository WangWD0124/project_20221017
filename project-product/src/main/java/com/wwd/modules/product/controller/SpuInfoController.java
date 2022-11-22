package com.wwd.modules.product.controller;

import com.wwd.common.annotation.LogOperation;
import com.wwd.common.constant.Constant;
import com.wwd.common.page.PageData;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.common.utils.ExcelUtils;
import com.wwd.common.utils.Result;
import com.wwd.common.validator.AssertUtils;
import com.wwd.common.validator.ValidatorUtils;
import com.wwd.common.validator.group.AddGroup;
import com.wwd.common.validator.group.DefaultGroup;
import com.wwd.common.validator.group.UpdateGroup;
import com.wwd.modules.product.dto.*;
import com.wwd.modules.product.entity.ProductAttrValueEntity;
import com.wwd.modules.product.entity.SpuImagesEntity;
import com.wwd.modules.product.excel.SpuInfoExcel;
import com.wwd.modules.product.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * spu信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("product/spuinfo")
@Api(tags="spu信息")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;


    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("product:spuinfo:page")
    public Result<PageData<SpuInfoDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SpuInfoDTO> page = spuInfoService.page(params);

        return new Result<PageData<SpuInfoDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("product:spuinfo:info")
    public Result<SpuInfoDTO> get(@PathVariable("id") Long id){
        SpuInfoDTO data = spuInfoService.get(id);

        return new Result<SpuInfoDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("product:spuinfo:save")
    public Result save(@RequestBody SpuInfoDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        //设置创建时间、修改时间(new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").format(new Date())）返回字符串
        dto.setCreateTime(new Date());
        dto.setUpdateTime(new Date());
        spuInfoService.save(dto);

        //保存 积分、成长值 至 sms_spu_bounds（coupon服务）pms_spu_info_desc
        SpuBoundsDTO spuBoundsDTO = dto.getBounds();
        spuBoundsDTO.setSpuId(dto.getId());//关联
        spuInfoDescService.save(spuInfoDescDTO);

        //保存 商品详情图集 至
        SpuInfoDescDTO spuInfoDescDTO = new SpuInfoDescDTO();
        spuInfoDescDTO.setSpuId(dto.getId());
        spuInfoDescDTO.setDecript(StringUtils.join(dto.getDecript(),","));//列表转换成逗号隔开的字符串
        spuInfoDescService.save(spuInfoDescDTO);

        //批量保存 商品图集 至 pms_spu_images
        List<SpuImagesEntity> spuImagesDTOList = dto.getImages().stream().map(img -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(dto.getId());//关联
            spuImagesEntity.setImgUrl(img);
            return spuImagesEntity;//TODO 值得学习总结！(stream遍历方式）
        }).collect(Collectors.toList());
        spuImagesService.insertBatch(spuImagesDTOList);//TODO 值得学习总结！(熟悉各层接口提供的CRUD方法）

        //保存 基本属性对应值 至 pms_product_attr_value
        List<ProductAttrValueEntity> productAttrValueEntities = dto.getBaseAttrs().stream().map(productAttrValueDTO -> {
            productAttrValueDTO.setSpuId(dto.getId());
            return ConvertUtils.sourceToTarget(productAttrValueDTO, ProductAttrValueEntity.class);
        }).collect(Collectors.toList());
        productAttrValueService.insertBatch(productAttrValueEntities);

        //保存 所有sku信息 至 pms_sku_info
        List<SkuInfoDTO> skuInfoDTOList = dto.getSkus();
        if (skuInfoDTOList != null && skuInfoDTOList.size() > 0){
            skuInfoDTOList.forEach(skuInfoDTO -> {
                String skuDefaultImg = "";
                for (SkuImagesDTO skuImagesDTO : skuInfoDTO.getImages()){

                    if (skuImagesDTO.getDefaultImg() == 1){
                        skuDefaultImg = skuImagesDTO.getImgUrl();
                        break;
                    }
                }
                skuInfoDTO.setSkuDefaultImg(skuDefaultImg);//遍历找出sku图片集默认图片
                skuInfoDTO.setSaleCount(0L);//初始销量0
                skuInfoDTO.setSpuId(dto.getId());
                skuInfoDTO.setCatalogId(dto.getCatalogId());
                skuInfoDTO.setBrandId(dto.getBrandId());
                skuInfoService.save(skuInfoDTO);
            });

        dto.getSkus();


        for (SkuInfoDTO skuInfoDTO : skuInfoDTOList){


        }
        //TODO 完善sku属性值保存

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("product:spuinfo:update")
    public Result update(@RequestBody SpuInfoDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        //设置修改时间
        //TODO
        spuInfoService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("product:spuinfo:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        spuInfoService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("product:spuinfo:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SpuInfoDTO> list = spuInfoService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SpuInfoExcel.class);
    }

}