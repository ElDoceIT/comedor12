package com.comedor.comedor.controller;
import com.comedor.comedor.model.Producto;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;




@Controller
@RequestMapping("/productos")
public class ProductoController {


    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private IProductoService productoService;

    @GetMapping("ver")
    public String productoVer(Model model, Pageable page) {
        //realiza la paginacion en 5 items
        Page<Producto> lista =productoService.buscarTodas(page);
        model.addAttribute("productos",lista);

        return "productos/producto_ver";
    }

    //Guardar nuevos y modificar productos
    @PostMapping("/save")
    public String guardarComida(Producto producto) {
        productoRepository.save(producto);

        return "redirect:/productos/ver";
    }


    @GetMapping("/search")
    public String listarProductos(
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @PageableDefault(size = 10) Pageable pageable, Model model) {

        Page<Producto> productos;

        if (descripcion != null && !descripcion.isEmpty()) {
            Producto productoExample = new Producto();
            productoExample.setDescripcion(descripcion);


            productos = productoService.buscarPorNombre(productoExample, pageable);
        } else {
            productos = productoService.buscarTodas(pageable);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("descripcion", descripcion);

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

}
