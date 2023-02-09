package com.wwd.modules.ware.controller;

import com.wwd.common.annotation.LogOperation;
import com.wwd.common.constant.Constant;
import com.wwd.common.feign.dto.ware.SkuHasStockVo;
import com.wwd.common.page.PageData;
import com.wwd.common.utils.ExcelUtils;
import com.wwd.common.utils.Result;
import com.wwd.common.validator.AssertUtils;
import com.wwd.common.validator.ValidatorUtils;
import com.wwd.common.validator.group.AddGroup;
import com.wwd.common.validator.group.DefaultGroup;
import com.wwd.common.validator.group.UpdateGroup;
import com.wwd.modules.ware.dto.WareSkuDTO;
import com.wwd.modules.ware.enume.WareEnume;
import com.wwd.modules.ware.excel.WareSkuExcel;
import com.wwd.modules.ware.exception.NotStockException;
import com.wwd.modules.ware.service.WareSkuService;
import com.wwd.modules.ware.vo.LockStockResult;
import com.wwd.modules.ware.vo.WareSkuLockVo;
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
 * 商品库存
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("ware/waresku")
@Api(tags="商品库存")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("ware:waresku:page")
    public Result<PageData<WareSkuDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<WareSkuDTO> page = wareSkuService.page(params);

        return new Result<PageData<WareSkuDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("ware:waresku:info")
    public Result<WareSkuDTO> get(@PathVariable("id") Long id){
        WareSkuDTO data = wareSkuService.get(id);

        return new Result<WareSkuDTO>().ok(data);
    }

    @PostMapping("stock")
    @ApiOperation("库存")
    @RequiresPermissions("ware:waresku:info")
    public Result<List<SkuHasStockVo>> getSkuHasStockVoByIds(@RequestBody List<Long> skuIds){

        List<SkuHasStockVo> skuHasStockVos = wareSkuService.getSkuHasStockVoByIds(skuIds);

        return new Result<List<SkuHasStockVo>>().ok(skuHasStockVos);
    }

    @PostMapping("lock/order")
    @ApiOperation("订单锁定库存")
    @RequiresPermissions("ware:waresku:info")
    public Result orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo) {

        try {
            Boolean lockStockResult = wareSkuService.orderLockStock(wareSkuLockVo);
            return new Result();
        } catch (NotStockException e) {
            e.printStackTrace();
            return new Result().error(WareEnume.NO_STOCK_EXCEPTION.getCode(), WareEnume.NO_STOCK_EXCEPTION.getMsg());
        }
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("ware:waresku:save")
    public Result save(@RequestBody WareSkuDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        wareSkuService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("ware:waresku:update")
    public Result update(@RequestBody WareSkuDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        wareSkuService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("ware:waresku:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        wareSkuService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("ware:waresku:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<WareSkuDTO> list = wareSkuService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, WareSkuExcel.class);
    }

}