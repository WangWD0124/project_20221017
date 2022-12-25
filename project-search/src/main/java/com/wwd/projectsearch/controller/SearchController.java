package com.wwd.projectsearch.controller;


import com.wwd.projectsearch.Vo.SearchParam;
import com.wwd.projectsearch.Vo.SearchResult;
import com.wwd.projectsearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping({"/","/list.html"})
    public String list(SearchParam searchParam, Model model) {

        SearchResult result = searchService.search(searchParam);
        model.addAttribute("result", result);
        return "list";

    }
}
