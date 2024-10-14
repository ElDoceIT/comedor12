package com.comedor.comedor.controller;


import com.comedor.comedor.model.Menu;
import com.comedor.comedor.repository.MenuRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MenuRepository menuRepository;

    @GetMapping("/")
    public String Home(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemana = inicioSemana.plusDays(4); // Asegurarse de que el fin de semana sea el viernes

        // Obtener todos los menús y filtrar por la semana actual
        List<Menu> todosLosMenus = menuRepository.findAll().stream()
                .filter(menu -> !menu.getFechaMenu().isBefore(inicioSemana) && !menu.getFechaMenu().isAfter(finSemana))
                .sorted(Comparator.comparingInt(menu -> {
                    switch (menu.getComida().getTipo_comida()) {
                        case 1: return 0; // Principal
                        case 2: return 1; // Ligth
                        case 3: return 2; // Celiaco
                        case 4: return 3; // Fruta
                        default: return 4; // Otros
                    }
                }))
                .collect(Collectors.toList());

        // Agrupar los menús por fecha
        Map<LocalDate, List<Menu>> menusPorDia = new LinkedHashMap<>();
        for (LocalDate dia = inicioSemana; dia.isBefore(finSemana.plusDays(1)); dia = dia.plusDays(1)) {
            LocalDate finalDia = dia;
            List<Menu> menusDelDia = todosLosMenus.stream()
                    .filter(menu -> menu.getFechaMenu().isEqual(finalDia))
                    .collect(Collectors.toList());
            if (!menusDelDia.isEmpty()) {
                menusPorDia.put(dia, menusDelDia);
            }
        }

        // Agregar los menús agrupados al modelo
        model.addAttribute("menusPorDia", menusPorDia);

        // Crear el rango de fechas para mostrar en la vista (de lunes a viernes)
        String rangoFechas = "del " + inicioSemana.format(DateTimeFormatter.ofPattern("dd/MM")) +
                " al " + finSemana.format(DateTimeFormatter.ofPattern("dd/MM"));
        model.addAttribute("rangoFechas", rangoFechas);

        return "menu/menu_semanal";
    }



    @GetMapping("/fer")
    public String HomeFer(){
        return "fer";
    }

    @GetMapping("/waldo")
    public String HomeWaldo(){
        return "waldo";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "DNI o contraseña incorrectos. Inténtalo de nuevo.");
        }
        return "login";  // Nombre de la vista del formulario de login
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        SecurityContextLogoutHandler logoutHandler =
                new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        return "redirect:/login";
    }





}
