package com.comedor.comedor.controller;


import com.comedor.comedor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/ver")
    public String consumosVer(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios/usuarios_ver";
    }
}
