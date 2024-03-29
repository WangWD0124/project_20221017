package com.wwd.modules.order.controller;

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
import com.wwd.modules.order.dto.OrderDTO;
import com.wwd.modules.order.excel.OrderExcel;
import com.wwd.modules.order.service.OrderService;
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
 * 订单
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("order/order")
@Api(tags="订单")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("order:order:page")
    public Result<PageData<OrderDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<OrderDTO> page = orderService.page(params);

        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("order:order:info")
    public Result<OrderDTO> get(@PathVariable("id") Long id){
        OrderDTO data = orderService.get(id);

        return new Result<OrderDTO>().ok(data);
    }

    @GetMapping("status/{orderSn}")
    @ApiOperation("订单状态信息")
    @RequiresPermissions("order:order:status")
    public Result<Integer> getStatusByOrderSn(@PathVariable("orderSn") String orderSn){
        Integer status = orderService.getStatusByOrderSn(orderSn);

        return new Result<Integer>().ok(status);
    }

    @GetMapping("updateStatus/{orderSn}/{status}")
    @ApiOperation("修改订单状态信息")
    @RequiresPermissions("order:order:status")
    public Result<Integer> updateStatusByOrderSn(@PathVariable("orderSn") String orderSn, @PathVariable("status") Integer status){
        Long res = orderService.updateStatusByOrderSn(orderSn, status);
        if (res != 1){
            return new Result().error(1, "订单关闭失败");
        }
        return new Result();
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("order:order:save")
    public Result save(@RequestBody OrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        orderService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("order:order:update")
    public Result update(@RequestBody OrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        orderService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("order:order:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        orderService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("order:order:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<OrderDTO> list = orderService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, OrderExcel.class);
    }

}