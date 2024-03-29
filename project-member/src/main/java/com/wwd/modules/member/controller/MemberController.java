package com.wwd.modules.member.controller;

import com.alibaba.fastjson.JSONObject;
import com.wwd.common.annotation.LogOperation;
import com.wwd.common.constant.Constant;
import com.wwd.common.page.PageData;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.common.utils.ExcelUtils;
import com.wwd.common.utils.Result;
import com.wwd.common.validator.AssertUtils;
import com.wwd.common.validator.ValidatorUtils;
import com.wwd.common.validator.group.AddGroup;
import com.wwd.common.validator.group.DefaultGroup;
import com.wwd.common.validator.group.UpdateGroup;
import com.wwd.modules.member.dto.MemberDTO;
import com.wwd.modules.member.dto.SocialUser;
import com.wwd.modules.member.dto.UserLoginDTO;
import com.wwd.modules.member.dto.UserRegistDTO;
import com.wwd.modules.member.entity.MemberEntity;
import com.wwd.modules.member.excel.MemberExcel;
import com.wwd.modules.member.exception.PhoneExsitException;
import com.wwd.modules.member.exception.UserNameExsitException;
import com.wwd.modules.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 会员
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@RequestMapping("member/member")
@Api(tags="会员")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    //@RequiresPermissions("member:member:page")
    public Result<PageData<MemberDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MemberDTO> page = memberService.page(params);

        return new Result<PageData<MemberDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    //@RequiresPermissions("member:member:info")
    public Result<MemberDTO> get(@PathVariable("id") Long id){
        MemberDTO data = memberService.get(id);

        return new Result<MemberDTO>().ok(data);
    }

//    @GetMapping("{userName}")
//    @ApiOperation("用户名查重")
//    //@RequiresPermissions("member:member:info")
//    public Result checkUserName(@PathVariable("userName") String userName){
//        try {
//            memberService.checkUserName(userName);
//        } catch (Exception e)
//
//        return new Result();
//    }
//
//    @GetMapping("{phone}")
//    @ApiOperation("手机号码查重")
//    //@RequiresPermissions("member:member:info")
//    public Result checkPhone(@PathVariable("phone") String phone){
//        memberService.checkPhone(phone);
//        return new Result();
//    }

    @PostMapping("register")
    @ApiOperation("注册")
    @LogOperation("注册")
    //@RequiresPermissions("member:member:register")
    public Result register(@RequestBody UserRegistDTO userRegistDTO){
        //效验数据
        ValidatorUtils.validateEntity(userRegistDTO, AddGroup.class, DefaultGroup.class);
        try {
            memberService.register(userRegistDTO);
        } catch (PhoneExsitException e){
            return new Result().error(1, e.getMessage());
        } catch (UserNameExsitException e){
            return new Result().error(2, e.getMessage());
        }
        return new Result();
    }

    @PostMapping("login")
    @ApiOperation("登录")
    @LogOperation("登录")
    //@RequiresPermissions("member:member:register")
    public Result login(@RequestBody UserLoginDTO userLoginDTO){
        //效验数据
        ValidatorUtils.validateEntity(userLoginDTO, AddGroup.class, DefaultGroup.class);

        MemberEntity memberEntity = memberService.login(userLoginDTO);
        MemberDTO memberDTO = ConvertUtils.sourceToTarget(memberEntity, MemberDTO.class);
        if (memberDTO != null){
            return new Result().ok(memberDTO);
        } else {
            return new Result().error(1, "账号或密码有误");
        }
    }

    @PostMapping("giteeInfo")
    @ApiOperation("社交登录")
    @LogOperation("社交登录")
    //@RequiresPermissions("member:member:register")
    public Result giteeInfo(@RequestBody SocialUser socialUser){

        MemberEntity memberEntity = memberService.giteeInfo(socialUser);
        MemberDTO memberDTO = ConvertUtils.sourceToTarget(memberEntity, MemberDTO.class);
        if (memberDTO != null){
            return new Result().ok(memberDTO);
        } else {
            return new Result().error(1, "gitee账号登录失败");
        }
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    //@RequiresPermissions("member:member:save")
    public Result save(@RequestBody MemberDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        memberService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    //@RequiresPermissions("member:member:update")
    public Result update(@RequestBody MemberDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        memberService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    //@RequiresPermissions("member:member:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        memberService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    //@RequiresPermissions("member:member:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<MemberDTO> list = memberService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, MemberExcel.class);
    }

}