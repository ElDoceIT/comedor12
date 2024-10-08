package com.comedor.comedor.controller;

import com.comedor.comedor.model.Comida;
import com.comedor.comedor.model.Menu;
import com.comedor.comedor.repository.ComidaRepository;
import com.comedor.comedor.repository.MenuRepository;
import com.comedor.comedor.service.IComidaService;
import com.comedor.comedor.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private IComidaService comidaService;


    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {
        List<Comida> listaComidas = comidaService.buscarTodas();
        List<Menu> menusNoProcesados = menuService.obtenerMenusNoProcesados();
        model.addAttribute("comidas", listaComidas);
        model.addAttribute("menus", menusNoProcesados);
        return "menu/menu_asignar";
    }

    @PostMapping("/guardar")
    public String guardarMenu(@RequestParam("fechaMenu") LocalDate fechaMenu,
                              @RequestParam("comidaId") Integer id_comida,
                              Model model, RedirectAttributes redirectAttributes) {

        // Validar que la fecha no sea anterior a la actual
        if (fechaMenu.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No puedes guardar un menú con una fecha anterior a la actual.");
            return "redirect:/menu/crear";
        }

        // Obtener la comida seleccionada
        Comida comida = comidaService.buscarPorId(id_comida);

        // Crear y guardar el nuevo menú
        Menu menu = new Menu();
        menu.setFechaMenu(fechaMenu);
        menu.setComida(comida);
        menu.setPublicar(0);  // Inicialmente no procesado
        menuService.guardar(menu);

        return "redirect:/menu/crear";
    }

    @PostMapping("/procesar")
    public String procesarMenus() {
        menuService.marcarMenusComoProcesados();
        return "redirect:/menu/crear";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMenu(@PathVariable("id") Integer id_menu) {
        menuService.eliminarPorId(id_menu);
        return "redirect:/menu/crear";
    }

    @GetMapping("/ver")
    public String listarMenuesFuturos(Model model) {
        List<Comida> listaComidas = comidaService.buscarTodas();
        model.addAttribute("comidas", listaComidas);
        LocalDate today = LocalDate.now();
        /*List<Menu> menus = menuRepository.findAll().stream()
                .filter(menu -> !menu.getFechaMenu().isBefore(today))
                .collect(Collectors.toList());*/
        List<Menu> menus =menuRepository.findAll().stream()
                .filter(menu -> !menu.getFechaMenu().isBefore(today)) // Filtrar por fecha >= hoy
                .sorted((menu1, menu2) -> menu1.getFechaMenu().compareTo(menu2.getFechaMenu())) // Ordenar por fecha ascendente
                .collect(Collectors.toList());

        model.addAttribute("menus",menus);
        return "menu/menu_ver";
    }

    @GetMapping("/vertodo")
    public String listarMenuesFuturos(Model model,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "15") int size) {
        //Pageable pageable = PageRequest.of(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMenu").descending());
        Page<Menu> menus = menuRepository.findAll(pageable);

        model.addAttribute("menus", menus);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", menus.getTotalPages());

        return "menu/menu_ver_todo";
    }

    @GetMapping("/semanal")
    public String  menuSemanal(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate lunes=hoy.with(java.time.DayOfWeek.MONDAY);
        LocalDate viernes=hoy.with(java.time.DayOfWeek.FRIDAY);

        List<Menu> menusDeLaSemana= menuService.getMenusSemana(lunes, viernes);

        Map<LocalDate, List<Menu>> menusPorDia = menusDeLaSemana.stream()
                .collect(Collectors.groupingBy(Menu::getFechaMenu));

        model.addAttribute("menusPorDia",menusPorDia);
        return "menu/menu_semanal";
    }

    @GetMapping("/buscarComidas")
    @ResponseBody
    public List<Comida> buscarComidas(@RequestParam("query") String query) {
        return comidaService.buscarPorNombre(query);
    }


}
