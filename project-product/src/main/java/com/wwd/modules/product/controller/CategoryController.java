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
import com.wwd.modules.product.dto.CategoryDTO;
import com.wwd.modules.product.excel.CategoryExcel;
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
 * 商品三级分类
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("product/category")
@Api(tags="商品三级分类")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询全部三级分类，并以树形结构返回
     */
    @GetMapping("/list")
    @ApiOperation("列表")
    @RequiresPermissions("product:category:list")
    public Result<List<CategoryDTO>> list() {
        List<CategoryDTO> list = categoryService.listWithTree();

        return new Result<List<CategoryDTO>>().ok(list);
    }


    @GetMapping("{id}")
    @ApiOperation("信息")
    //@RequiresPermissions("product:category:info")
    public Result<CategoryDTO> get(@PathVariable("id") Long id){
        CategoryDTO data = categoryService.get(id);

        return new Result<CategoryDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("product:category:save")
    public Result save(@RequestBody CategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        categoryService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    //@RequiresPermissions("product:category:update")
    public Result update(@RequestBody CategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        categoryService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    //@RequiresPermissions("product:category:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        categoryService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("product:category:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<CategoryDTO> list = categoryService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, CategoryExcel.class);
    }

}