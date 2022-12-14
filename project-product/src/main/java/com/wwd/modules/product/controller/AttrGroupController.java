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
import com.wwd.modules.product.dto.AttrDTO;
import com.wwd.modules.product.dto.AttrGroupDTO;
import com.wwd.modules.product.excel.AttrGroupExcel;
import com.wwd.modules.product.service.AttrGroupService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("product/attrgroup")
@Api(tags="属性分组")
public class AttrGroupController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("page/{catelog_id}")
    @ApiOperation("根据三级分类查询分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("product:attrgroup:page")
    public Result<PageData<AttrGroupDTO>> pageByCatelog_id(@ApiIgnore @RequestParam Map<String, Object> params, @PathVariable("catelog_id") Long catelog_id){
        PageData<AttrGroupDTO> page = attrGroupService.page(params, catelog_id);

        return new Result<PageData<AttrGroupDTO>>().ok(page);
    }

    @GetMapping("list/withAttr/{catelog_id}")
    @ApiOperation("根据三级分类查询属性组包含各个属性")
    @RequiresPermissions("product:attrgroup:list")
    public Result<List<AttrGroupDTO>> listWithAttrByCatelog_id(@PathVariable("catelog_id") Long catelog_id){

        //根据catelog_id查询三级分类下的属性分组列表
        List<AttrGroupDTO> attrGroupDTOList = attrGroupService.listWithAttrByCatelog_id(catelog_id);

        //根据attrGroupId查询每一个属性分组下的属性列表
        List<AttrDTO> attrDTOList = new ArrayList<>();

        for (AttrGroupDTO attrGroupDTO : attrGroupDTOList) {
            //每一个属性分组下的属性列表（包含基本属性、销售属性）
            attrDTOList = attrService.getByAttrGroupId(attrGroupDTO.getAttrGroupId());
            attrGroupDTO.setAttrs(attrDTOList);
        }

        return new Result<List<AttrGroupDTO>>().ok(attrGroupDTOList);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("product:attrgroup:info")
    public Result<AttrGroupDTO> get(@PathVariable("id") Long id){
        AttrGroupDTO data = attrGroupService.get(id);

        //用于修改页面渲染级联选择器：分类路径
        Long[] catelogPath = categoryService.findCatelogPath(data.getCatelogId());

        data.setCatelogPath(catelogPath);

        return new Result<AttrGroupDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("product:attrgroup:save")
    public Result save(@RequestBody AttrGroupDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        attrGroupService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("product:attrgroup:update")
    public Result update(@RequestBody AttrGroupDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        attrGroupService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("product:attrgroup:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        attrGroupService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("product:attrgroup:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<AttrGroupDTO> list = attrGroupService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, AttrGroupExcel.class);
    }

}