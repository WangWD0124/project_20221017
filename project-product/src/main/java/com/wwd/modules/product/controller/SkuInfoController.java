package com.wwd.modules.product.controller;

import com.wwd.common.annotation.LogOperation;
import com.wwd.common.constant.Constant;
import com.wwd.common.page.PageData;
import com.wwd.common.utils.ExcelUtils;
import com.wwd.common.utils.Result;
import com.wwd.common.validator.AssertUtils;
import com.wwd.common.validator.ValidatorUtils;
import com.wwd.common.validator.group.AddGroup;
import com.wwd.common.validator.group.DefaultGroup;
import com.wwd.common.validator.group.UpdateGroup;
import com.wwd.modules.product.dto.SkuInfoDTO;
import com.wwd.modules.product.excel.SkuInfoExcel;
import com.wwd.modules.product.service.SkuInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * sku信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("product/skuinfo")
@Api(tags="sku信息")
public class SkuInfoController {
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
    @RequiresPermissions("product:skuinfo:page")
    public Result<PageData<SkuInfoDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SkuInfoDTO> page = skuInfoService.page(params);

        return new Result<PageData<SkuInfoDTO>>().ok(page);
    }

    @GetMapping("page/search")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("product:skuinfo:search")
    public Result<PageData<SkuInfoDTO>> search(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SkuInfoDTO> page = skuInfoService.search(params);

        return new Result<PageData<SkuInfoDTO>>().ok(page);
    }

    @GetMapping("price/{id}")
    @ApiOperation("价格")
    @RequiresPermissions("product:skuinfo:info")
    public Result<BigDecimal> getPrice(@PathVariable("id") Long id){
        SkuInfoDTO data = skuInfoService.get(id);
        BigDecimal price = data.getPrice();
        return new Result<BigDecimal>().ok(price);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("product:skuinfo:info")
    public Result<SkuInfoDTO> get(@PathVariable("id") Long id){
        SkuInfoDTO data = skuInfoService.get(id);

        return new Result<SkuInfoDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("product:skuinfo:save")
    public Result save(@RequestBody SkuInfoDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        skuInfoService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("product:skuinfo:update")
    public Result update(@RequestBody SkuInfoDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        skuInfoService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("product:skuinfo:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        skuInfoService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("product:skuinfo:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SkuInfoDTO> list = skuInfoService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SkuInfoExcel.class);
    }

}