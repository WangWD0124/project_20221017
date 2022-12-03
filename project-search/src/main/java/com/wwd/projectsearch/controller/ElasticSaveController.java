package com.wwd.projectsearch.controller;

import com.wwd.common.annotation.LogOperation;
import com.wwd.common.es.SkuEsModel;
import com.wwd.common.utils.Result;
import com.wwd.projectsearch.service.ProductSaveService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/search")
@RestController
@Slf4j
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    //上架商品
    @PostMapping("/product")
    @ApiOperation("保存")
    @LogOperation("保存")
    public Result productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList){

        boolean R = false;
        try {
            R = productSaveService.productStatusUp(skuEsModelList);
        } catch (Exception e){
            log.error("商品上架异常", e);
            return new Result().error("商品上架异常");
        }
        if (!R){
            return new Result().ok(R);
        }else {
            return new Result().error("商品上架异常");
        }

    }
}
