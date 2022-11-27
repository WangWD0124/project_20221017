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
import com.wwd.modules.ware.dto.PurchaseDetailDTO;
import com.wwd.modules.ware.dto.WareInfoDTO;
import com.wwd.modules.ware.entity.PurchaseEntity;
import com.wwd.modules.ware.excel.PurchaseDetailExcel;
import com.wwd.modules.ware.service.PurchaseDetailService;
import com.wwd.modules.ware.service.PurchaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("ware/purchasedetail")
@Api(tags="")
public class PurchaseDetailController {
    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("ware:purchasedetail:page")
    public Result<PageData<PurchaseDetailDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<PurchaseDetailDTO> page = purchaseDetailService.page(params);

        return new Result<PageData<PurchaseDetailDTO>>().ok(page);
    }

    @GetMapping("page/search")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("ware:purchasedetail:page")
    public Result<PageData<PurchaseDetailDTO>> search(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<PurchaseDetailDTO> page = purchaseDetailService.search(params);

        return new Result<PageData<PurchaseDetailDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("ware:purchasedetail:info")
    public Result<PurchaseDetailDTO> get(@PathVariable("id") Long id){
        PurchaseDetailDTO data = purchaseDetailService.get(id);

        return new Result<PurchaseDetailDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("ware:purchasedetail:save")
    public Result save(@RequestBody PurchaseDetailDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        purchaseDetailService.save(dto);

        return new Result();
    }

    @PostMapping("merge")
    @ApiOperation("合并")
    @LogOperation("合并")
    @RequiresPermissions("ware:purchasedetail:merge")
    public Result merge(@RequestBody MergeDTO mergeDTO){

        //1、判断采购单purchaseId是否存在
        // 存在：
        // 1）状态”新建“：修改采购需求purchaseId
        // 2）状态非”新建“：返回提示是否创建新的采购单，是则创建新的采购单，修改采购需求purchaseId
        // 不存在：
        // 1）返回提示是否创建新的采购单，是则创建新的采购单，修改采购需求purchaseId
        if (!mergeDTO.getIsNew()){
            Long purchaseId = mergeDTO.getPurchaseId();
            PurchaseEntity purchaseEntity = purchaseService.selectById(purchaseId);
            if (purchaseEntity != null){
                if (purchaseEntity.getStatus() == PurchaseController.purchase_ststus.CREATED.getCode() ||
                        purchaseEntity.getStatus() == PurchaseController.purchase_ststus.ASSIGNED.getCode()){
                    List<Long> ids = mergeDTO.getIds();
                    purchaseDetailService.merge(ids, purchaseId);
                    return new Result();
                } else {
                    return new Result().error(1, "id为"+purchaseId+"的采购单["+PurchaseController.purchase_ststus.StatusString(purchaseEntity.getStatus())+"]，无法合并，是否创建新的采购单同时合并？");
                }
            } else {
                return new Result().error(2, "id为"+purchaseId+"的采购单不存在，是否创建新的采购单同时合并？");
            }
        } else {
            //新建采购单
            PurchaseDTO purchaseDTO = new PurchaseDTO();
            //purchaseDTO.setWareId();TODO 新建的采购单绑定的仓库id
            purchaseDTO.setStatus(PurchaseController.purchase_ststus.CREATED.getCode());
            purchaseDTO.setCreateTime(new Date());
            purchaseDTO.setUpdateTime(new Date());
            purchaseService.save(purchaseDTO);
            List<Long> ids = mergeDTO.getIds();
            purchaseDetailService.merge(ids, purchaseDTO.getId());
            return new Result();
        }

    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("ware:purchasedetail:update")
    public Result update(@RequestBody PurchaseDetailDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        purchaseDetailService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("ware:purchasedetail:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        purchaseDetailService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("ware:purchasedetail:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<PurchaseDetailDTO> list = purchaseDetailService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, PurchaseDetailExcel.class);
    }

}
//用于接收json数据
@Data
class MergeDTO{
    private List<Long> ids;
    private Long purchaseId;
    private Boolean isNew;
}