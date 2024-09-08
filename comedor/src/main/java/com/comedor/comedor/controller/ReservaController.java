package com.comedor.comedor.controller;


import com.comedor.comedor.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;


    @GetMapping("/ver")
    public String reservas(Model model) {
        model.addAttribute("reservas", reservaRepository.findAll());
        return "reservas/reservas_ver";
    }
}
