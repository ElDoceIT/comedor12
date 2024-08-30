package com.comedor.comedor.controller;


import com.comedor.comedor.model.Comida;
import com.comedor.comedor.repository.ComidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comida")
public class ComidasController {

    @Autowired
    private ComidaRepository comidaRepository;

    @GetMapping("/ver")
    public String VerComidas(Model model){
        model.addAttribute("comidas", comidaRepository.findAll());
        return "comidas_ver";
    }

    @GetMapping("/new")
    public String guardarComida(){
        return "comida_new";
    }

    @PostMapping("/save")
    public String guardarComida(Comida comida){
        comidaRepository.save(comida);
        return "redirect:/comida/ver";
    }
    
}
