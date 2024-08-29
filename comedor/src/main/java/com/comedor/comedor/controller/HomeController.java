package com.comedor.comedor.controller;


import com.comedor.comedor.model.Comida;
import com.comedor.comedor.repository.ComidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {



    @GetMapping("/")
    public String Home(){
        return "Home";
    }




}
