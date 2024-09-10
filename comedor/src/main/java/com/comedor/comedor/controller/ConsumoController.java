package com.comedor.comedor.controller;

import com.comedor.comedor.model.Consumo;
import com.comedor.comedor.model.Producto;
import com.comedor.comedor.repository.ConsumoRepository;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoRepository consumoRepository;


    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @GetMapping("/ver")
    public String consumosVer(Model model) {
        model.addAttribute("consumos", consumoRepository.findAll());
        return "consumos/consumos_ver";

    }

    @GetMapping("/verfiltrados")
    public String consumosVerFiltrados(Model model) {
        model.addAttribute("consumos", consumoRepository.findAll());
        return "consumos/consumos_filtrados";

    }

    @GetMapping("/asignar")
    public String consumosAsignar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "consumos/consumos_asignar";

    }

    @PostMapping("/save")
    public String guardarConsumo(Consumo consumo) {
        consumoRepository.save(consumo);
        return "redirect:/consumos/ver";
    }

    @GetMapping("/editar/{id}")
    public String editarConsumo(@PathVariable("id") int idConsumo, Model model) {
        Consumo consumo = consumoRepository.findById(idConsumo).get();
        model.addAttribute("consumo",consumo);
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "consumos/consumos_editar";
    }

    @GetMapping("/delete/{id}")
    public String eliminarConsumo(@PathVariable("id") int IdConsumo) {
        consumoRepository.deleteById(IdConsumo);
        return "redirect:/consumos/ver";
    }
}
