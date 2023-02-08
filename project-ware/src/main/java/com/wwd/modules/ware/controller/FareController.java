package com.wwd.modules.ware.controller;

import com.wwd.common.utils.Result;
import com.wwd.modules.ware.service.FareService;
import com.wwd.modules.ware.vo.FareVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 物流地址与运费信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@RestController
@Api(tags="物流地址与运费信息")
public class FareController {

    @Autowired
    private FareService fareService;

    @GetMapping("fare/{addId}")
    @ResponseBody
    public Result<FareVo> getFare(@PathVariable("addrId") Long addrId){
        FareVo fare = fareService.getFare(addrId);
        return new Result<FareVo>().ok(fare);
    }
}
