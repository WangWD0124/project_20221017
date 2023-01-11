package com.wwd.projectauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping({"/", "/login.html"})
    public String login(){
        return "login";
    }

    @GetMapping({"/", "/reg.html"})
    public String reg(){
        return "reg";
    }
}
