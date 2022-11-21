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
import com.wwd.modules.product.dto.AttrAttrgroupRelationDTO;
import com.wwd.modules.product.dto.AttrDTO;
import com.wwd.modules.product.excel.AttrExcel;
import com.wwd.modules.product.service.AttrAttrgroupRelationService;
import com.wwd.modules.product.service.AttrService;
import com.wwd.modules.product.service.CategoryService;
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
 * 商品属性
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("product/attr")
@Api(tags="商品属性")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @GetMapping("{attrType}/page/{catelog_id}")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("product:attr:page")
    public Result<PageData<AttrDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params, @PathVariable("attrType") String attrType, @PathVariable("catelog_id") Long catelog_id){
        PageData<AttrDTO> page = attrService.page(params, attrType, catelog_id);

        return new Result<PageData<AttrDTO>>().ok(page);
    }

    @GetMapping("{attrType}/list/{catelog_id}")
    @ApiOperation("列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("product:attr:page")
    public Result<List<AttrDTO>> list(@ApiIgnore @RequestParam Map<String, Object> params, @PathVariable("attrType") String attrType, @PathVariable("catelog_id") Long catelog_id){
        List<AttrDTO> list = attrService.list(attrType, catelog_id);

        return new Result<List<AttrDTO>>().ok(list);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("product:attr:info")
    public Result<AttrDTO> get(@PathVariable("id") Long id){
        AttrDTO data = attrService.get(id);

        //用于修改页面渲染级联选择器：分类路径
        Long[] catelogPath = categoryService.findCatelogPath(data.getCatelogId());
        data.setCatelogPath(catelogPath);

        //其中的attrGroupId代表的属性分组和属性关联另外保存
        Long attrGroupId = attrAttrgroupRelationService.findAttrGroupId(data.getAttrId());
        data.setAttrGroupId(attrGroupId);

        return new Result<AttrDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("product:attr:save")
    public Result save(@RequestBody AttrDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        attrService.save(dto);

        //而且基本属性和属性分组关联另外保存，但销售属性不用
        if (dto.getAttrType() == 1){
            AttrAttrgroupRelationDTO relationDTO = new AttrAttrgroupRelationDTO();
            relationDTO.setAttrId(dto.getAttrId());
            relationDTO.setAttrGroupId(dto.getAttrGroupId());
            relationDTO.setAttrSort(0);
            attrAttrgroupRelationService.save(relationDTO);
        }
        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("product:attr:update")
    public Result update(@RequestBody AttrDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        attrService.update(dto);

        //而且基本属性和属性分组关联另外修改，但销售属性不用
        if (dto.getAttrType() == 1){
            AttrAttrgroupRelationDTO relationDTO = attrAttrgroupRelationService.getByAttrId(dto.getAttrId());
            if (!relationDTO.getAttrGroupId().equals(dto.getAttrGroupId())){
                //TODO (排序）
                //if (!relationDTO.getAttrSort().equals(dto.getAttrSort()))
                relationDTO.setAttrGroupId(dto.getAttrGroupId());
                attrAttrgroupRelationService.update(relationDTO);
            }
        }
        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("product:attr:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        attrService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("product:attr:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<AttrDTO> list = attrService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, AttrExcel.class);
    }

}