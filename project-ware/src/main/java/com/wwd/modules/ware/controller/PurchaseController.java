package com.wwd.modules.ware.controller;

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
import com.wwd.modules.ware.dto.PurchaseDTO;
import com.wwd.modules.ware.excel.PurchaseExcel;
import com.wwd.modules.ware.service.PurchaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("ware/purchase")
@Api(tags="采购信息")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    enum purchase_ststus {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        PURCHASING(2, "正在采购"),
        COMPLETED(3, "已完成"),
        PURCHASING_FAILED(4, "采购失败");

        private int code;
        private String status;
        purchase_ststus(int code, String status){
            this.code = code;
            this.status = status;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        public static String StatusString(int code){
            switch (code){
                case 0:
                    return CREATED.getStatus();
                case 1:
                    return ASSIGNED.getStatus();
                case 2:
                    return PURCHASING.getStatus();
                case 3:
                    return COMPLETED.getStatus();
                case 4:
                    return PURCHASING_FAILED.getStatus();
                default:
                    return null;
            }
        }
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("ware:purchase:page")
    public Result<PageData<PurchaseDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<PurchaseDTO> page = purchaseService.page(params);

        return new Result<PageData<PurchaseDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("ware:purchase:info")
    public Result<PurchaseDTO> get(@PathVariable("id") Long id){
        PurchaseDTO data = purchaseService.get(id);

        return new Result<PurchaseDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("ware:purchase:save")
    public Result save(@RequestBody PurchaseDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        dto.setCreateTime(new Date());

        dto.setUpdateTime(new Date());

        purchaseService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("ware:purchase:update")
    public Result update(@RequestBody PurchaseDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        purchaseService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("ware:purchase:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        purchaseService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("ware:purchase:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<PurchaseDTO> list = purchaseService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, PurchaseExcel.class);
    }

}