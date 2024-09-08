package com.comedor.comedor.controller;


import com.comedor.comedor.repository.ConsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoRepository consumoRepository;


    @GetMapping("/ver")
    public String consumosVer(Model model) {
        model.addAttribute("consumos", consumoRepository.findAll());
        return "consumos/consumos_ver";

    }

    @GetMapping("/asignar")
    public String consumosAsignar(){
        return "consumos/consumos_asignar";

    }
}
