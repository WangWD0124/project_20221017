package com.wwd.modules.coupon.controller;

import com.wwd.common.annotation.LogOperation;
import com.wwd.common.constant.Constant;
import com.wwd.common.feign.dto.coupon.SkuFullReductionDTO;
import com.wwd.common.page.PageData;
import com.wwd.common.utils.ExcelUtils;
import com.wwd.common.utils.Result;
import com.wwd.common.validator.AssertUtils;
import com.wwd.common.validator.ValidatorUtils;
import com.wwd.common.validator.group.AddGroup;
import com.wwd.common.validator.group.DefaultGroup;
import com.wwd.common.validator.group.UpdateGroup;
import com.wwd.modules.coupon.excel.SkuFullReductionExcel;
import com.wwd.modules.coupon.service.SkuFullReductionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 商品满减信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("coupon/skufullreduction")
@Api(tags="商品满减信息")
public class SkuFullReductionController {
    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("coupon:skufullreduction:page")
    public Result<PageData<SkuFullReductionDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SkuFullReductionDTO> page = skuFullReductionService.page(params);

        return new Result<PageData<SkuFullReductionDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("coupon:skufullreduction:info")
    public Result<SkuFullReductionDTO> get(@PathVariable("id") Long id){
        SkuFullReductionDTO data = skuFullReductionService.get(id);

        return new Result<SkuFullReductionDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("coupon:skufullreduction:save")
    public Result save(@RequestBody SkuFullReductionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        skuFullReductionService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("coupon:skufullreduction:update")
    public Result update(@RequestBody SkuFullReductionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        skuFullReductionService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("coupon:skufullreduction:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        skuFullReductionService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("coupon:skufullreduction:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SkuFullReductionDTO> list = skuFullReductionService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SkuFullReductionExcel.class);
    }

}