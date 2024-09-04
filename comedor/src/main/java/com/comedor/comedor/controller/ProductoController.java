package com.comedor.comedor.controller;

import com.comedor.comedor.model.Comida;
import com.comedor.comedor.model.Producto;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private IProductoService productoService;

    @GetMapping("ver")
    public String productoVer(Model model) {
        model.addAttribute("productos",productoRepository.findAll());
        return "productos/producto_ver";
    }

    @PostMapping("/save")
    public String guardarComida(Producto producto) {
        productoRepository.save(producto);

        return "redirect:/productos/ver";
    }

    @GetMapping("/search")
    public String buscar(@ModelAttribute("search") Producto producto, Model model) {
        //System.out.println("buscando por Producto");
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("descripcion",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Producto> example = Example.of(producto, matcher);
        List<Producto> lista = productoService.buscarByExample(example);
        model.addAttribute("productos",lista);
        return "productos/producto_ver";
    }

    @ModelAttribute
    public void setGenericos(Model model){
        Producto productoSearch = new Producto();
        model.addAttribute("search",productoSearch);
    }
}
