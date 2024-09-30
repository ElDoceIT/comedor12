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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


    @GetMapping("/ver")
    public String reservas(Model model) {
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("serverTime", now);
        model.addAttribute("reservas", reservaRepository.findAll());
        return "reservas/reservas_ver";
    }

    @PostMapping("/guardar")
    public String guardarReserva(@ModelAttribute ReservaForm reservaForm, Principal principal) {
        String nombreUsuario = principal.getName(); // Devuelve un String
        Integer dni = null;

        try {
            dni = Integer.parseInt(nombreUsuario); // Intenta convertir a Integer
        } catch (NumberFormatException e) {
            // Manejar el caso cuando no se pueda convertir a un Integer
            System.out.println("El nombre de usuario no es un número válido: " + e.getMessage());
            // Puedes devolver un error, lanzar una excepción personalizada o manejar el caso de manera adecuada
        }

        Usuario usuario = usuarioService.obtenerPorDni(dni);
        // obtener el usuario logueado mediante el principal o servicio de usuario
        reservaService.guardarReserva(reservaForm, usuario);

        return "redirect:/reservas/ver";
    }
    @GetMapping("/reservar")
    public String reservar() {


        return "reservas/reserva_seleccionar";
    }

    @GetMapping("/reservas-semanales")
    public String obtenerReservasSemanales(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Convertir el username que es en realidad el dni, a Integer
        Integer dni = Integer.parseInt(userDetails.getUsername());
        LocalDate hoy = LocalDate.now();
        LocalDate lunes=hoy.with(java.time.DayOfWeek.MONDAY);
        LocalDate viernes=hoy.with(java.time.DayOfWeek.FRIDAY);



        List<Menu> menusDeLaSemana= menuService.getMenusSemana(lunes, viernes);


        LocalDate iniciosemana=hoy.with(DayOfWeek.SUNDAY);
        LocalDate findesemana=hoy.with(DayOfWeek.SATURDAY);

        List<Reserva> todasLasReservas = reservaRepository.findAll();

        Map<LocalDate, List<Menu>> menusPorDia = menusDeLaSemana.stream()
                .collect(Collectors.groupingBy(Menu::getFechaMenu));

        List<Reserva> reservasFiltradas = todasLasReservas.stream()
                .filter(reserva -> reserva.getUsuario().getDni().equals(dni)) // Filtrar por usuario
                //.filter(reserva -> reserva.getMenu().getFechaMenu().isAfter(iniciosemana.minusDays(1))

                        //no me hace falta el fin, tomo como reserva de domingo a futuro.
                        // && reserva.getMenu().getFechaMenu().isBefore(findesemana.plusDays(1))
                  //      ) // Filtrar por fecha
                .collect(Collectors.toList());


       // Reserva reservas= new Reserva();


        model.addAttribute("reservas", reservasFiltradas);
        

        model.addAttribute("menusPorDia",menusPorDia);
        return "/menu/menu_semanal";
    }

    // aca chequeo mis propias reservas de la semana.
    @GetMapping("/misreservas")
    public String MisReservas(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Integer dni = Integer.parseInt(userDetails.getUsername());
        LocalDate hoy = LocalDate.now();
        LocalDate iniciosemana=hoy.with(DayOfWeek.SUNDAY);
        LocalDate findesemana=hoy.with(DayOfWeek.SATURDAY);
        List<Reserva> todasLasReservas = reservaRepository.findAll();

        List<Reserva> reservasFiltradas = todasLasReservas.stream()
                .filter(reserva -> reserva.getUsuario().getDni().equals(dni)) // Filtrar por usuario
                //.filter(reserva -> reserva.getMenu().getFechaMenu().isAfter(iniciosemana.minusDays(1))

                //no me hace falta el fin, tomo como reserva de domingo a futuro.
                // && reserva.getMenu().getFechaMenu().isBefore(findesemana.plusDays(1))
                //      ) // Filtrar por fecha
                .collect(Collectors.toList());
        model.addAttribute("reservas", reservasFiltradas);
        return "/reservas/reservas_ver";
    }
}
