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
import java.time.LocalDateTime;
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
        LocalDateTime ahora = LocalDateTime.now();

        // Definir 9:00 AM del sábado de la semana actual
        LocalDateTime sabadoNueveAM = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).atTime(9, 0);

        // Si es sábado después de las 9:00 AM o domingo, ajustar a la semana siguiente
        if ((hoy.getDayOfWeek() == DayOfWeek.SATURDAY && ahora.isAfter(sabadoNueveAM)) || hoy.getDayOfWeek() == DayOfWeek.SUNDAY) {
            hoy = hoy.with(TemporalAdjusters.next(DayOfWeek.MONDAY)); // Mover a la semana siguiente
        }

        // Calcular el rango desde el lunes de la semana actual hasta el domingo de la próxima semana
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemana = inicioSemana.plusDays(13); // Desde lunes hasta domingo de la siguiente semana

        // Obtener todos los menús y filtrar por las dos semanas, condición publicar=1 y solo desde hoy en adelante
        LocalDate finalHoy = hoy;
        List<Menu> todosLosMenus = menuRepository.findAll().stream()
                .filter(menu -> !menu.getFechaMenu().isBefore(finalHoy)) // Mostrar solo menús desde hoy
                .filter(menu -> !menu.getFechaMenu().isAfter(finSemana)) // Hasta el fin de las dos semanas
                .filter(menu -> menu.getPublicar() == 1) // Solo menús públicos
                .sorted(Comparator.comparingInt(menu -> {
                    switch (menu.getComida().getTipo_comida()) {
                        case 1: return 0; // Principal
                        case 2: return 1; // Light
                        case 3: return 2; // Celiaco
                        case 4: return 3; // Fruta
                        default: return 4; // Otros
                    }
                }))
                .collect(Collectors.toList());

        // Agrupar los menús por fecha
        Map<LocalDate, List<Menu>> menusPorDia = new LinkedHashMap<>();
        for (LocalDate dia = hoy; dia.isBefore(finSemana.plusDays(1)); dia = dia.plusDays(1)) {
            LocalDate finalDia = dia;
            List<Menu> menusDelDia = todosLosMenus.stream()
                    .filter(menu -> menu.getFechaMenu().isEqual(finalDia))
                    .collect(Collectors.toList());
            if (!menusDelDia.isEmpty()) {
                menusPorDia.put(dia, menusDelDia);
            }
        }

        // Si no hay menús disponibles, agregar mensaje "Próximamente"
        if (menusPorDia.isEmpty()) {
            model.addAttribute("mensaje", "Próximamente");
        } else {
            model.addAttribute("menusPorDia", menusPorDia);
        }

        // Crear el rango de fechas para mostrar en la vista (desde hoy hasta el último día con menús disponibles)
        String rangoFechas = "del " + hoy.format(DateTimeFormatter.ofPattern("dd/MM")) +
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
        Integer dni = Integer.parseInt(userDetails.getUsername());
        LocalDate hoy = LocalDate.now();
        LocalTime horaActual = LocalTime.now();

        // Calcular lunes y domingo de la semana actual
        LocalDate lunesActual = hoy.with(DayOfWeek.MONDAY);
        LocalDate domingoActual = hoy.with(DayOfWeek.SUNDAY);

        // Determinar si es sábado después de las 9:00 AM o domingo
        boolean esFinDeSemanaExtendido = (hoy.getDayOfWeek() == DayOfWeek.SATURDAY && horaActual.isAfter(LocalTime.of(9, 0))) ||
                hoy.getDayOfWeek() == DayOfWeek.SUNDAY;

        // Ajustar el rango de fechas: desde el lunes actual hasta el domingo de la siguiente semana
        LocalDate lunesFiltro = esFinDeSemanaExtendido ? lunesActual.plusWeeks(1) : lunesActual;
        LocalDate domingoFiltro = lunesFiltro.plusDays(13); // 2 semanas completas

        // Obtener los menús desde el lunes actual hasta el domingo de la próxima semana
        List<Menu> menusDisponibles = menuService.getMenusSemana(lunesFiltro, domingoFiltro);

        // Obtener todas las reservas del usuario dentro del rango
        List<Reserva> reservasUsuario = reservaService.obtenerReservasEntreFechasPorUsuario(dni, lunesFiltro, domingoFiltro);

        // Filtrar los menús para excluir:
        // 1. Menús ya reservados por el usuario.
        // 2. Menús de días anteriores.
        // 3. Menús del día actual si es después de las 9:00 AM.
        List<Menu> menusFiltrados = menusDisponibles.stream()
                .filter(menu -> reservasUsuario.stream()
                        .noneMatch(reserva -> reserva.getMenu().getFechaMenu().equals(menu.getFechaMenu()))
                )
                .filter(menu -> !menu.getFechaMenu().isBefore(hoy)) // Excluir días anteriores
                .filter(menu -> !(menu.getFechaMenu().isEqual(hoy) && horaActual.isAfter(LocalTime.of(9, 0)))) // Excluir día actual después de las 9 AM
                .collect(Collectors.toList());

        // Agrupar los menús filtrados por día de la semana
        Map<LocalDate, List<Menu>> menusPorDia = menusFiltrados.stream()
                .collect(Collectors.groupingBy(Menu::getFechaMenu,
                        () -> new TreeMap<>(Comparator.naturalOrder()),
                        Collectors.toList()));

        // Ordenar las reservas para la vista "Mis Reservas" desde la semana actual hasta la próxima semana
        List<Reserva> reservasFiltradas = reservasUsuario.stream()
                .sorted(Comparator.comparing((Reserva reserva) -> reserva.getMenu().getFechaMenu()).reversed())
                .collect(Collectors.toList());

        // Agregar los datos al modelo
        model.addAttribute("reservas", reservasFiltradas);
        model.addAttribute("menusPorDia", menusPorDia);

        return "reserva/reservar";
    }





}
