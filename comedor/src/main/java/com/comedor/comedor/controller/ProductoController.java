package com.comedor.comedor.controller;

import com.comedor.comedor.model.Producto;
import com.comedor.comedor.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("ver")
    public String productoVer(Model model) {
        model.addAttribute("productos",productoRepository.findAll());
        return "productos/producto_ver";
    }

    @GetMapping("/new")
    public String productoNew() {
        return "productos/producto_new";
    }
}
