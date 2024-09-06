package com.comedor.comedor.controller;

import com.comedor.comedor.model.Comida;
import com.comedor.comedor.model.Producto;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private IProductoService productoService;

    @GetMapping("ver")
    public String productoVer(Model model, Pageable page) {
        model.addAttribute("productos",productoRepository.findAll());
        Page<Producto> lista =productoService.buscarTodas(page);
        model.addAttribute("pagina",lista);
        return "productos/producto_ver";
    }

    @PostMapping("/save")
    public String guardarComida(Producto producto) {
        productoRepository.save(producto);

        return "redirect:/productos/ver";
    }

    //metodo encargado de realizar la busqueda dentro de productos
    @GetMapping("/search")
    public String buscar(@ModelAttribute("search") Producto producto, Model model) {
        //buscar cualquier coincidencia
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("descripcion",ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Producto> example = Example.of(producto, matcher);
        List<Producto> lista = productoService.buscarByExample(example);
        model.addAttribute("productos",lista);
        return "productos/producto_ver";
    }

    //metodo encargado de editar los productos
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id_Producto, Model model) {
        Producto producto = productoRepository.findById(id_Producto).get();
        model.addAttribute("producto",producto);
        return "productos/producto_editar";


    }

    @ModelAttribute
    public void setGenericos(Model model){
        Producto productoSearch = new Producto();
        model.addAttribute("search",productoSearch);
    }

    @GetMapping("/verpaginte")
    public String mostrarPaginado(Model model, Pageable page){
        Page<Producto> lista =productoService.buscarTodas(page);
        model.addAttribute("pagina",lista);
        return "productos/producto_ver";
    }
}
