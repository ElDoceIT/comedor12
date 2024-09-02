package com.comedor.comedor.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {



    @GetMapping("/")
    public String Home(){
        return "home";
    }

    @GetMapping("/fer")
    public String HomeFer(){
        return "fer";
    }

    @GetMapping("/waldo")
    public String HomeWaldo(){
        return "waldo";
    }



}
