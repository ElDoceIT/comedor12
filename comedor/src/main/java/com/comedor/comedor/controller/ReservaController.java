package com.comedor.comedor.controller;


import com.comedor.comedor.dto.ReservaForm;
import com.comedor.comedor.model.Comida;
import com.comedor.comedor.model.Menu;
import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.ReservaRepository;
import com.comedor.comedor.service.IMenuService;
import com.comedor.comedor.service.IReservaService;
import com.comedor.comedor.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private IReservaService reservaService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IMenuService menuService;

//muestra todas las reservas del usuario.
@GetMapping("/ver")
public String reservas(Model model) {
    LocalDateTime now = LocalDateTime.now();

    // Obtener todas las reservas y ordenarlas por la fecha del menú
    List<Reserva> reservasOrdenadas = reservaRepository.findAll().stream()
            .sorted((r1, r2) -> r2.getMenu().getFechaMenu().compareTo(r1.getMenu().getFechaMenu())) // Ordenar por fechaMenu descendente
            .collect(Collectors.toList());

    // Agregar la hora del servidor y las reservas ordenadas al modelo
    model.addAttribute("serverTime", now);
    model.addAttribute("reservas", reservasOrdenadas);

    return "reservas/reservas_ver";
}



    @PostMapping("/reservar")
    public String reservar(
            @RequestParam("menuSeleccionado") Integer idMenu,
            @RequestParam("tipoComida") Integer medio,
            Principal principal, // Para obtener al usuario logueado
            Model model
    ) {
        // Obtener la hora actual y validar que es antes de las 9 AM
        LocalDateTime now = LocalDateTime.now();
        LocalTime nineAM = LocalTime.of(9, 0);

        // Obtener el menú seleccionado
        Menu menu = menuService.buscarPorId(idMenu);
        LocalDate fechaMenu = menu.getFechaMenu();

        // Verificar que la hora actual no sea después de las 9AM del día del menú
        if (now.isAfter(fechaMenu.atTime(nineAM))) {
            model.addAttribute("error", "No se puede reservar este menú, ya ha pasado el límite de las 9AM.");
            return "reservas/reserva_menu_semanal"; // Mantener la misma página
        }

        // Obtener el usuario logueado
        Integer username = Integer.valueOf(principal.getName());
        Usuario usuario = usuarioService.obtenerPorDni(username);

        // Verificar si el usuario ya tiene una reserva para esa fecha
        boolean yaReservado = reservaService.existeReservaParaUsuarioYFecha(usuario, fechaMenu);

        if (yaReservado) {
            // Si el usuario ya tiene una reserva para este día, mostrar el mensaje de error
            model.addAttribute("errorReservaDuplicada", "Ya has reservado un menú para este día.");
            return "reservas/reserva_menu_semanal"; // Mantener la misma página
        }

        // Crear y guardar la nueva reserva
        Reserva reserva = new Reserva();
        reserva.setMenu(menu);
        reserva.setUsuario(usuario);
        reserva.setF_reserva(now);
        reserva.setMedio(medio);

        reservaService.guardar(reserva);

        List<Reserva> reservasUsuario = reservaService.obtenerReservasPorUsuario(usuario);
        model.addAttribute("reservasUsuario", reservasUsuario);

        return "redirect:/reservas/reservas-semanales"; // Redirigir a la página de reservas si la reserva es exitosa
    }




@GetMapping("/reservas-semanales")
public String obtenerReservasSemanales(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    // Convertir el username (dni) a Integer
    Integer dni = Integer.parseInt(userDetails.getUsername());
    LocalDate hoy = LocalDate.now();
    LocalTime horaActual = LocalTime.now();

    // Calcular el lunes y viernes de la semana actual
    LocalDate lunes = hoy.with(java.time.DayOfWeek.MONDAY);
    LocalDate viernes = hoy.with(java.time.DayOfWeek.FRIDAY);

    // Si es viernes después de las 9 AM, empezar a mostrar los menús de la próxima semana
    if (hoy.getDayOfWeek() == DayOfWeek.FRIDAY && horaActual.isAfter(LocalTime.of(9, 0))) {
        lunes = lunes.plusWeeks(1);   // Mover a la próxima semana
        viernes = viernes.plusWeeks(1);
    }

    // Obtener los menús de la semana (o de la siguiente semana si es viernes después de las 9 AM)
    List<Menu> menusDeLaSemana = menuService.getMenusSemana(lunes, viernes);

    // Agrupar los menús por día
    Map<LocalDate, List<Menu>> menusPorDia = menusDeLaSemana.stream()
            // Filtrar los menús del día actual si es después de las 9 AM
            .filter(menu -> {
                if (menu.getFechaMenu().isEqual(hoy) && horaActual.isAfter(LocalTime.of(9, 0))) {
                    return false; // Si es el día actual y son más de las 9 AM, no lo mostramos
                }
                return true;
            })
            .collect(Collectors.groupingBy(Menu::getFechaMenu));

    // Obtener las reservas del usuario
    List<Reserva> todasLasReservas = reservaRepository.findAll();
    List<Reserva> reservasFiltradas = todasLasReservas.stream()
            .filter(reserva -> reserva.getUsuario().getDni().equals(dni))
            .collect(Collectors.toList());

    // Agregar los menús y reservas al modelo
    model.addAttribute("reservas", reservasFiltradas);
    model.addAttribute("menusPorDia", menusPorDia);

    return "/reservas/reserva_menu_semanal";
}


    // aca chequeo mis propias reservas de la semana.
    @GetMapping("/misreservas")
    public String MisReservas(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Integer dni = Integer.parseInt(userDetails.getUsername());

        // Obtener todas las reservas y filtrarlas por el DNI del usuario
        List<Reserva> todasLasReservas = reservaRepository.findAll();
        List<Reserva> reservasFiltradas = todasLasReservas.stream()
                .filter(reserva -> reserva.getUsuario().getDni().equals(dni)) // Filtrar por usuario
                .sorted((r1, r2) -> r2.getMenu().getFechaMenu().compareTo(r1.getMenu().getFechaMenu())) // Ordenar por fechaMenu descendente
                .collect(Collectors.toList());

        // Imprimir las fechas de los menús en la consola
        reservasFiltradas.forEach(reserva -> {
            System.out.println("Fecha Menu: " + reserva.getMenu().getFechaMenu());
        });

        model.addAttribute("reservas", reservasFiltradas);
        return "/reservas/reservas_ver";
    }
        //desde la vista de menu semanal
    @PostMapping("/eliminar/{id}")
    public String eliminarReserva(@PathVariable("id") Integer idReserva, RedirectAttributes redirectAttributes, Model model) {
        // Obtiene la reserva
        Reserva reserva = reservaService.buscarPorId(idReserva);

        if (reserva != null) {
            // Obtiene el menú relacionado con la reserva
            Menu menu = menuService.buscarPorId(reserva.getMenu().getId_menu());

            if (menu != null) {
                LocalDate fechaMenu = menu.getFechaMenu(); // Asumiendo que 'fechaMenu' es de tipo LocalDate
                LocalTime horaLimite = LocalTime.of(9, 0); // 9:00 AM
                LocalDateTime fechaActual = LocalDateTime.now(); // Fecha y hora actual

                // Si el menú corresponde al mismo día y la hora actual es posterior a las 9 AM
                if ((fechaMenu.equals(fechaActual.toLocalDate()) && fechaActual.toLocalTime().isAfter(horaLimite))
                        || fechaMenu.isBefore(fechaActual.toLocalDate())) {
                    // Prohibir la eliminación y mostrar un mensaje de error
                    System.out.println("no se puede eliminar despues de las 9 ");

                    redirectAttributes.addFlashAttribute("error", "No se puede eliminar o modificar la reserva después de las 9.");
                    return "redirect:/reservas/reservas-semanales";

                    // Redirige a la página de reservas semanales
                } else {
                    // Permite la eliminación si la fecha del menú es posterior a la actual o si es hoy antes de las 9 AM
                    reservaService.eliminarPorId(idReserva);
                    return "redirect:/reservas/reservas-semanales";
                    //return "reservas/reserva_seleccionar";
                }
            } else {
                // Manejar caso donde el menú no exista
                model.addAttribute("error", "El menú asociado a la reserva no existe.");
                return "redirect:/reservas/reservas-semanales";
            }
        } else {
            // Manejar caso donde la reserva no exista
            model.addAttribute("error", "La reserva no existe.");
            return "redirect:/reservas/reservas-semanales";
        }
    }
    //desde la vista mismenus.
    @PostMapping("/eliminarUser/{id}")
    public String eliminarReservaUser(@PathVariable("id") Integer idReserva, RedirectAttributes redirectAttributes, Model model) {
        // Obtiene la reserva
        Reserva reserva = reservaService.buscarPorId(idReserva);

        if (reserva != null) {
            // Obtiene el menú relacionado con la reserva
            Menu menu = menuService.buscarPorId(reserva.getMenu().getId_menu());

            if (menu != null) {
                LocalDate fechaMenu = menu.getFechaMenu(); // Asumiendo que 'fechaMenu' es de tipo LocalDate
                LocalTime horaLimite = LocalTime.of(9, 0); // 9:00 AM
                LocalDateTime fechaActual = LocalDateTime.now(); // Fecha y hora actual

                // Si el menú corresponde al mismo día y la hora actual es posterior a las 9 AM
                if ((fechaMenu.equals(fechaActual.toLocalDate()) && fechaActual.toLocalTime().isAfter(horaLimite))
                        || fechaMenu.isBefore(fechaActual.toLocalDate())) {
                    // Prohibir la eliminación y mostrar un mensaje de error
                    System.out.println("no se puede eliminar despues de las 9");

                    redirectAttributes.addFlashAttribute("error", "No se puede eliminar o modificar la reserva después de las 9 del dia correspondiente al menu, lo mismo que no se pueden eliminar fechas de menus antiguos..");
                    return "redirect:/reservas/ver";

                    // Redirige a la página de reservas semanales
                } else {
                    // Permite la eliminación si la fecha del menú es posterior a la actual o si es hoy antes de las 9 AM
                    reservaService.eliminarPorId(idReserva);
                    return "redirect:/reservas/ver";
                    //return "reservas/reserva_seleccionar";
                }
            } else {
                // Manejar caso donde el menú no exista
                model.addAttribute("error", "El menú asociado a la reserva no existe.");
                return "redirect:/reservas/ver";
            }
        } else {
            // Manejar caso donde la reserva no exista
            model.addAttribute("error", "La reserva no existe.");
            return "redirect:/reservas/ver";
        }
    }
}
