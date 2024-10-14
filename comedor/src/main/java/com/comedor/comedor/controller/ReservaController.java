package com.comedor.comedor.controller;

import com.comedor.comedor.model.Menu;
import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.ReservaRepository;
import com.comedor.comedor.service.IMenuService;
import com.comedor.comedor.service.IReservaService;
import com.comedor.comedor.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import java.util.*;
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

        // Si es viernes después de las 9 AM, sábado o domingo, empezar a mostrar los menús de la próxima semana
        if ((hoy.getDayOfWeek() == DayOfWeek.FRIDAY && horaActual.isAfter(LocalTime.of(9, 0))) ||
                hoy.getDayOfWeek() == DayOfWeek.SATURDAY ||
                hoy.getDayOfWeek() == DayOfWeek.SUNDAY) {

            lunes = lunes.plusWeeks(1);   // Mover a la próxima semana
            viernes = viernes.plusWeeks(1);
        }

        // Obtener los menús de la semana (o de la siguiente semana si es viernes después de las 9 AM, sábado o domingo)
        List<Menu> menusDeLaSemana = menuService.getMenusSemana(lunes, viernes);

        // Agrupar los menús por día y ordenar por los días de la semana
        Map<LocalDate, List<Menu>> menusPorDia = menusDeLaSemana.stream()
                // Filtrar los menús del día actual si es después de las 9 AM
                .filter(menu -> !(menu.getFechaMenu().isEqual(hoy) && horaActual.isAfter(LocalTime.of(9, 0))))
                .collect(Collectors.groupingBy(Menu::getFechaMenu,
                        () -> new TreeMap<>(Comparator.comparingInt(date -> date.getDayOfWeek().getValue())),
                        Collectors.toList()));

        // Obtener las reservas del usuario
        List<Reserva> todasLasReservas = reservaRepository.findAll();
        List<Reserva> reservasFiltradas = todasLasReservas.stream()
                .filter(reserva -> reserva.getUsuario().getDni().equals(dni))
                // Ordenar por la fecha del menú en orden descendente
                .sorted(Comparator.comparing((Reserva reserva) -> reserva.getMenu().getFechaMenu()).reversed())
                .collect(Collectors.toList());
        // Agregar los menús y reservas al modelo
        model.addAttribute("reservas", reservasFiltradas);
        model.addAttribute("menusPorDia", menusPorDia);

        return "/reservas/reserva_menu_semanal";
    }






    // aca chequeo mis propias reservas.
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


    //aca muestro las reservas que vera el comedor para el dia del menu.
    @GetMapping("/reservas-dia-actual")
    public String obtenerReservasDelDia(Model model) {
        LocalDate hoy = LocalDate.now();

        // Mapas para las descripciones de tipo de comida y medio
        Map<Integer, String> tipoComidaDescripcion = Map.of(
                1, "Principal",
                2, "Light",
                3, "Celiaco",
                4, "Fruta"
        );

        Map<Integer, String> medioDescripcion = Map.of(
                1, "Comedor",
                2, "Retira",
                3, "Vianda"
        );

        // Obtener todas las reservas cuya fecha de menú sea el día actual
        List<Reserva> reservasDelDia = reservaRepository.findAll().stream()
                .filter(reserva -> reserva.getMenu().getFechaMenu().equals(hoy))
                .collect(Collectors.toList());

        // Calcular la suma de cada tipo de comida
        Map<Integer, Long> sumaPorTipoComida = reservasDelDia.stream()
                .collect(Collectors.groupingBy(reserva -> reserva.getMenu().getComida().getTipo_comida(), Collectors.counting()));

        // Calcular la suma por lugar de consumo
        Map<Integer, Long> sumaPorLugarConsumo = reservasDelDia.stream()
                .collect(Collectors.groupingBy(Reserva::getMedio, Collectors.counting()));

        // Ordenar las reservas por apellido, nombre y menú principal
        reservasDelDia.sort(Comparator.comparing((Reserva r) -> r.getUsuario().getApellido())
                .thenComparing(r -> r.getUsuario().getNombre())
                .thenComparing(r -> r.getMenu().getComida().getPrincipal()));

        // Total de reservas
        long totalReservas = reservasDelDia.size();

        // Contar reservas consumidas (entregado != null)
        long totalConsumidas = reservasDelDia.stream()
                .filter(reserva -> reserva.getEntregado() != null)
                .count();

        // Agregar los resultados y descripciones al modelo
        model.addAttribute("reservas", reservasDelDia);
        model.addAttribute("sumaPorTipoComida", sumaPorTipoComida);
        model.addAttribute("sumaPorLugarConsumo", sumaPorLugarConsumo);
        model.addAttribute("tipoComidaDescripcion", tipoComidaDescripcion);
        model.addAttribute("medioDescripcion", medioDescripcion);
        model.addAttribute("totalReservas", totalReservas);
        model.addAttribute("totalConsumidas", totalConsumidas);

        return "reservas/reservas_dia";  // Nombre de la vista
    }



    @PostMapping("/marcar-consumido/{id}")
   public String marcarComoConsumido(@PathVariable("id") Integer idReserva) {
       Optional<Reserva> reservaOpt = reservaRepository.findById(idReserva);
       if (reservaOpt.isPresent()) {
           Reserva reserva = reservaOpt.get();
           reserva.setEntregado(LocalDateTime.now());  // Marcar con fecha y hora actual
           reservaRepository.save(reserva);  // Guardar la actualización
       }
       return "redirect:/reservas/reservas-dia-actual";  // Redirigir a la misma vista
   }


}
