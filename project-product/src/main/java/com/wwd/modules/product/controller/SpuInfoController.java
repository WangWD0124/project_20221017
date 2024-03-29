package com.wwd.modules.product.controller;

import com.wwd.common.annotation.LogOperation;
import com.wwd.common.constant.Constant;
import com.wwd.common.feign.dto.coupon.MemberPriceDTO;
import com.wwd.common.feign.dto.coupon.SkuFullReductionDTO;
import com.wwd.common.feign.dto.coupon.SkuLadderDTO;
import com.wwd.common.feign.dto.coupon.SpuBoundsDTO;
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
import com.wwd.modules.product.feign.CouponFeignService;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO 高级部分完成事务管理
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
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;


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

    @GetMapping("page/search")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("product:spuinfo:page")
    public Result<PageData<SpuInfoDTO>> search(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SpuInfoDTO> page = spuInfoService.search(params);

        return new Result<PageData<SpuInfoDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("product:spuinfo:info")
    public Result<SpuInfoDTO> get(@PathVariable("id") Long id){
        SpuInfoDTO data = spuInfoService.get(id);

        return new Result<SpuInfoDTO>().ok(data);
    }

    @GetMapping("skuId/{id}")
    @ApiOperation("信息getSpuInfoBySkuId")
    @RequiresPermissions("product:spuinfo:info")
    public Result<SpuInfoDTO> getSpuInfoBySkuId(@PathVariable("id") Long id){
        SpuInfoDTO data = spuInfoService.getSpuInfoBySkuId(id);

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

        //保存 积分、成长值 至 sms_spu_bounds（coupon服务）
        SpuBoundsDTO spuBoundsDTO = dto.getBounds();
        spuBoundsDTO.setSpuId(dto.getId());//关联
        couponFeignService.save(spuBoundsDTO);

        //保存 商品详情图集 至 pms_spu_info_desc
        SpuInfoDescDTO spuInfoDescDTO = new SpuInfoDescDTO();
        spuInfoDescDTO.setSpuId(dto.getId());
        spuInfoDescDTO.setDecript(StringUtils.join(dto.getDecript(),","));//列表转换成逗号隔开的字符串
        spuInfoDescService.save(spuInfoDescDTO);

        //批量保存 商品图集 至 pms_spu_images//TODO 前端图片上传问题
        List<SpuImagesEntity> spuImagesDTOList = dto.getImages().stream().map(img -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(dto.getId());//关联
            spuImagesEntity.setImgUrl(img);
            return spuImagesEntity;
        }).collect(Collectors.toList());
        spuImagesService.insertBatch(spuImagesDTOList);

        //批量保存 基本属性对应值 至 pms_product_attr_value
        List<ProductAttrValueEntity> productAttrValueEntities = dto.getBaseAttrs().stream().map(productAttrValueDTO -> {
            productAttrValueDTO.setSpuId(dto.getId());
            return ConvertUtils.sourceToTarget(productAttrValueDTO, ProductAttrValueEntity.class);
        }).collect(Collectors.toList());
        productAttrValueService.insertBatch(productAttrValueEntities);

        //保存 所有sku信息 主要部分至 pms_sku_info
        List<SkuInfoDTO> skuInfoDTOList = dto.getSkus();
        if (skuInfoDTOList != null && skuInfoDTOList.size() > 0) {
            skuInfoDTOList.forEach(skuInfoDTO -> {
                String skuDefaultImg = "";
                for (SkuImagesDTO skuImagesDTO : skuInfoDTO.getImages()) {
                    //保存 sku图片集 至 pms_sku_images
                    skuImagesDTO.setSkuId(skuInfoDTO.getSkuId());//TODO 前端图片上传问题
                    skuImagesService.save(skuImagesDTO);
                    if (skuImagesDTO.getDefaultImg() == 1) {
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

                //保存 sku属性及其值 至 pms_sku_sale_attr_value
                for (SkuSaleAttrValueDTO skuSaleAttrValueDTO : skuInfoDTO.getAttr()) {
                    skuSaleAttrValueDTO.setSkuId(skuInfoDTO.getSkuId());
                    skuSaleAttrValueService.save(skuSaleAttrValueDTO);
                }

                //保存满几件打折 至 sms_sku_ladder（coupon服务）
                if (skuInfoDTO.getFullCount() > 0){ //有效信息才保存
                    SkuLadderDTO skuLadderDTO = new SkuLadderDTO();
                    skuLadderDTO.setSkuId(skuInfoDTO.getSkuId());
                    skuLadderDTO.setFullCount(skuInfoDTO.getFullCount());
                    skuLadderDTO.setDiscount(skuInfoDTO.getDiscount());
                    skuLadderDTO.setAddOther(skuInfoDTO.getCountStatus());
                    couponFeignService.save(skuLadderDTO);
                }

                //保存满几件打折 至 sms_sku_ladder（coupon服务）
                if (skuInfoDTO.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    SkuFullReductionDTO skuFullReductionDTO = new SkuFullReductionDTO();
                    skuFullReductionDTO.setSkuId(skuInfoDTO.getSkuId());
                    skuFullReductionDTO.setFullPrice(skuInfoDTO.getFullPrice());
                    skuFullReductionDTO.setReducePrice(skuInfoDTO.getReducePrice());
                    skuFullReductionDTO.setAddOther(skuInfoDTO.getFullCount());
                    couponFeignService.save(skuFullReductionDTO);
                }

                //保存会员价 至 sms_member_price（coupon服务）
                for (MemberPriceDTO memberPriceDTO : skuInfoDTO.getMemberPrice()) {
                    if (memberPriceDTO.getMemberPrice().compareTo(new BigDecimal("0")) >  0){
                        memberPriceDTO.setSkuId(skuInfoDTO.getSkuId());
                        couponFeignService.save(memberPriceDTO);
                    }
                }

            });
        }

        return new Result();
    }

    @PostMapping("{spuId}/up")
    @ApiOperation("修改-商品上架")
    @LogOperation("修改-商品上架")
    @RequiresPermissions("product:spuinfo:productUp")
    public Result productUp(@PathVariable("spuId") Long spuId){

        spuInfoService.updatePublishStatusById(spuId);

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
        dto.setUpdateTime(new Date());

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
