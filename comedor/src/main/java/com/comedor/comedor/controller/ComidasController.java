package com.comedor.comedor.controller;


import com.comedor.comedor.model.Comida;
import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.ComidaRepository;
import com.comedor.comedor.service.IComidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/comida")
public class ComidasController {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private IComidaService comidaService;
    @Autowired
    private JsonComponentModule jsonComponentModule;


    @GetMapping("/ver")
    public String VerComidas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Comida> comidaPage = comidaRepository.findAll(pageable);

        model.addAttribute("comidas", comidaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comidaPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "comida/comida_ver";
    }
    @GetMapping("/new")
    public String guardarComida(){
        return "comida/comida_new";
    }

    @PostMapping("/save")
    public String guardarComida(Comida comida){
        comidaRepository.save(comida);
        return "redirect:/comida/ver";
    }

    @GetMapping("/editar/{id}")
    public String editarComida(@PathVariable("id") Integer id_comida, Model model) {
        Comida comidas = comidaRepository.findById(id_comida).get();
        model.addAttribute("comidas",comidas);
        return "comida/comida_editar";
    }

    @GetMapping("/search")
    public String listarComida(
            @RequestParam(value = "principal", required = false) String principal,
            @PageableDefault(size = 10) Pageable pageable, Model model) {

        Page<Comida> comidas;

        if (principal != null && !principal.isEmpty()) {
            comidas = comidaService.buscarPorPrincipal(principal, pageable);
        }
        else {
            //comida = comidaRepository.findAll(pageable);
            comidas= comidaService.buscarTodos(pageable);
        }


        // Añadir atributos al modelo para ser usados en la vista
        model.addAttribute("comidas", comidas);
        model.addAttribute("principal", principal);
        model.addAttribute("currentPage", comidas.getNumber());
        model.addAttribute("totalPages", comidas.getTotalPages());
        model.addAttribute("totalItems", comidas.getTotalElements());
        model.addAttribute("pageSize", comidas.getSize());


        return "comida/comida_ver";
    }
    
}
