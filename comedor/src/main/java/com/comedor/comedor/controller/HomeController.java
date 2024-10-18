package com.comedor.comedor.controller;


import com.comedor.comedor.model.Menu;
import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.repository.MenuRepository;
import com.comedor.comedor.repository.ReservaRepository;
import com.comedor.comedor.service.IMenuService;
import com.comedor.comedor.service.IReservaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private IReservaService reservaService;

    //Aca veo el menu semanal de lunes a viernes
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

    @GetMapping("/waldo")
    public String HomeWaldo(){
        return "waldo";
    }

    //pantalla de login
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "DNI o contraseña incorrectos. Inténtalo de nuevo.");
        }
        return "login";  // Nombre de la vista del formulario de login
    }
    //Cierre de seccion
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        SecurityContextLogoutHandler logoutHandler =
                new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        return "redirect:/login";
    }

    //pantalla para realizar las reservas de los menus semanales
    @GetMapping("/reserva")
    public String reservasSemanal(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Convertir el username (dni) a Integer
        Integer dni = Integer.parseInt(userDetails.getUsername());
        LocalDate hoy = LocalDate.now();
        LocalTime horaActual = LocalTime.now();

        // Calcular el lunes y viernes de la semana actual
        LocalDate lunes = hoy.with(java.time.DayOfWeek.MONDAY);
        LocalDate viernes = hoy.with(java.time.DayOfWeek.FRIDAY);

        // Si es viernes después de las 9 AM, sábado o domingo, empezar a mostrar los menús de la próxima semana
        if ((hoy.getDayOfWeek() == DayOfWeek.FRIDAY && horaActual.isAfter(LocalTime.of(9, 0))) ||
                hoy.getDayOfWeek() == DayOfWeek.SATURDAY ||
                hoy.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lunes = lunes.plusWeeks(1);   // Mover a la próxima semana
            viernes = viernes.plusWeeks(1);
        }

        // Obtener los menús de la semana (o de la siguiente semana si es viernes después de las 9 AM, sábado o domingo)
        List<Menu> menusDeLaSemana = menuService.getMenusSemana(lunes, viernes);

        // Obtener las reservas del usuario actual
        List<Reserva> reservasUsuario = reservaService.obtenerReservasSemanalesPorUsuario(dni);

        // Filtrar los menús de días anteriores, del día actual después de las 9 AM y los menús ya reservados por el usuario
        List<Menu> menusFiltrados = menusDeLaSemana.stream()
                .filter(menu -> {
                    // No mostrar menús de días anteriores ni del día actual si es después de las 9 AM
                    if (menu.getFechaMenu().isBefore(hoy) ||
                            (menu.getFechaMenu().isEqual(hoy) && horaActual.isAfter(LocalTime.of(9, 0)))) {
                        return false;
                    }
                    // No mostrar menús de días donde el usuario ya reservó
                    return reservasUsuario.stream()
                            .noneMatch(reserva -> reserva.getMenu().getFechaMenu().equals(menu.getFechaMenu()));
                })
                .collect(Collectors.toList());

        // Agrupar los menús filtrados por día y ordenar por los días de la semana
        Map<LocalDate, List<Menu>> menusPorDia = menusFiltrados.stream()
                .collect(Collectors.groupingBy(Menu::getFechaMenu,
                        () -> new TreeMap<>(Comparator.comparingInt(date -> date.getDayOfWeek().getValue())),
                        Collectors.toList()));

        // Ordenar las reservas por la fecha del menú en orden descendente
        List<Reserva> reservasFiltradas = reservasUsuario.stream()
                .sorted(Comparator.comparing((Reserva reserva) -> reserva.getMenu().getFechaMenu()))
                .collect(Collectors.toList());

        // Agregar los menús y reservas al modelo
        model.addAttribute("reservas", reservasFiltradas);
        model.addAttribute("menusPorDia", menusPorDia);

        return "home";
    }

}
