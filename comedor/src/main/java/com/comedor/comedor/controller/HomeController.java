package com.comedor.comedor.controller;


import com.comedor.comedor.repository.ComidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ComidaRepository comidaRepository;

    @GetMapping("/")
    public String Home(){
        return "Home";
    }

    @GetMapping("/vercomidas")
    public String VerComidas(Model model){
        model.addAttribute("comidas", comidaRepository.findAll());
        return "verComidas";
    }
}
