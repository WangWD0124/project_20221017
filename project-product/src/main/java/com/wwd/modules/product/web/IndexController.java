package com.wwd.modules.product.web;

import com.wwd.modules.product.dto.CategoryDTO;
import com.wwd.modules.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping({"/", "/index.html"})
    public String index(Model model) {

        //查询一级分类
        List<CategoryDTO> categoryDTOList = categoryService.getCategoryLevel1();
        model.addAttribute("categoryLevel1", categoryDTOList);
        return "index";
    }

    @RequestMapping("index/json/catelog.json")
    @ResponseBody
    public Map<String, Object> getCatelogJson() {

        return categoryService.getCatelogJson();
    }
}
