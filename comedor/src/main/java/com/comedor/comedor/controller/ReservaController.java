package com.comedor.comedor.controller;

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
import java.time.format.DateTimeFormatter;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
            return "home"; // Mantener la misma página
        }

        // Obtener el usuario logueado
        Integer username = Integer.valueOf(principal.getName());
        Usuario usuario = usuarioService.obtenerPorDni(username);

        // Verificar si el usuario ya tiene una reserva para esa fecha
        boolean yaReservado = reservaService.existeReservaParaUsuarioYFecha(usuario, fechaMenu);

        if (yaReservado) {
            // Si el usuario ya tiene una reserva para este día, mostrar el mensaje de error
            model.addAttribute("errorReservaDuplicada", "Ya has reservado un menú para este día.");
            return "home"; // Mantener la misma página
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

        return "redirect:/reserva"; // Redirigir a la página de reservas si la reserva es exitosa
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

    @GetMapping("/asistencia")
    public String asistenciaUsuarios(Model model) {
        // Obtener todas las reservas
        List<Reserva> reservasMes = reservaRepository.findAll();

        // Agrupar reservas por usuario y por día
        Map<Usuario, Map<LocalDate, String>> asistenciaPorUsuario = new HashMap<>();

        // Crear una lista de fechas con menús disponibles
        List<LocalDate> fechasConMenu = new ArrayList<>();

        // Obtener el mes actual y las fechas de lunes a viernes
        LocalDate now = LocalDate.now();
        for (int day = 1; day <= now.lengthOfMonth(); day++) {
            LocalDate fecha = LocalDate.of(now.getYear(), now.getMonth(), day);
            if (fecha.getDayOfWeek().getValue() >= 1 && fecha.getDayOfWeek().getValue() <= 5) { // Lunes a Viernes
                fechasConMenu.add(fecha);
            }
        }

        for (Reserva reserva : reservasMes) {
            // Obtener la fecha del menú
            LocalDate fechaMenu = reserva.getMenu().getFechaMenu();

            // Solo considerar fechas que están en el mes actual y en los días con menú
            if (fechasConMenu.contains(fechaMenu)) {
                Usuario usuario = reserva.getUsuario();
                String estadoReserva;

                // Determinar el estado de la reserva
                if (reserva.getEntregado() == null) {
                    estadoReserva = "No Asistió";
                } else {
                    estadoReserva = reserva.getEntregado().toString(); // Asegúrate de que esto sea un String adecuado
                }

                // Agrupar reservas
                asistenciaPorUsuario
                        .computeIfAbsent(usuario, k -> new HashMap<>())
                        .put(fechaMenu, estadoReserva); // Almacena directamente
            }
        }

        // Rellenar usuarios sin reservas
        for (Usuario usuario : asistenciaPorUsuario.keySet()) {
            for (LocalDate fecha : fechasConMenu) {
                asistenciaPorUsuario.get(usuario).putIfAbsent(fecha, "No Reservó");
            }
        }

        // Agregar los datos al modelo
        model.addAttribute("asistenciaPorUsuario", asistenciaPorUsuario);
        model.addAttribute("fechasConMenu", fechasConMenu); // Solo días con menús

        return "reservas/reserva_asistencia";
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
                    return "redirect:/reserva";

                    // Redirige a la página de reservas semanales
                } else {
                    // Permite la eliminación si la fecha del menú es posterior a la actual o si es hoy antes de las 9 AM
                    reservaService.eliminarPorId(idReserva);
                    return "redirect:/reserva";
                    //return "reservas/reserva_seleccionar";
                }
            } else {
                // Manejar caso donde el menú no exista
                model.addAttribute("error", "El menú asociado a la reserva no existe.");
                return "redirect:/reserva";
            }
        } else {
            // Manejar caso donde la reserva no exista
            model.addAttribute("error", "La reserva no existe.");
            return "redirect:/reserva";
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

        // Filtrar reservas consumidas y no consumidas
        List<Reserva> reservasNoConsumidas = reservasDelDia.stream()
                .filter(reserva -> reserva.getEntregado() == null)
                .collect(Collectors.toList());

        List<Reserva> reservasConsumidas = reservasDelDia.stream()
                .filter(reserva -> reserva.getEntregado() != null)
                .collect(Collectors.toList());

        // Calcular la suma de cada tipo de comida
        Map<Integer, Long> sumaPorTipoComida = reservasDelDia.stream()
                .collect(Collectors.groupingBy(reserva -> reserva.getMenu().getComida().getTipo_comida(), Collectors.counting()));

        // Calcular la suma por lugar de consumo
        Map<Integer, Long> sumaPorLugarConsumo = reservasDelDia.stream()
                .collect(Collectors.groupingBy(Reserva::getMedio, Collectors.counting()));

        // Ordenar las reservas por apellido, nombre y menú principal
        reservasNoConsumidas.sort(Comparator.comparing((Reserva r) -> r.getUsuario().getApellido())
                .thenComparing(r -> r.getUsuario().getNombre())
                .thenComparing(r -> r.getMenu().getComida().getPrincipal()));

        // Total de reservas
        long totalReservas = reservasDelDia.size();

        // Contar reservas consumidas (entregado != null)
        long totalConsumidas = reservasDelDia.stream()
                .filter(reserva -> reserva.getEntregado() != null)
                .count();

        // Agregar los resultados y descripciones al modelo
        model.addAttribute("reservasNoConsumidas", reservasNoConsumidas);
        model.addAttribute("reservasConsumidas", reservasConsumidas);
        model.addAttribute("sumaPorTipoComida", sumaPorTipoComida);
        model.addAttribute("sumaPorLugarConsumo", sumaPorLugarConsumo);
        model.addAttribute("tipoComidaDescripcion", tipoComidaDescripcion);
        model.addAttribute("medioDescripcion", medioDescripcion);
        model.addAttribute("totalReservas", totalReservas);
        model.addAttribute("totalConsumidas", totalConsumidas);

        return "reservas/reservas_dia";  // Nombre de la vista
    }






 //Marco el usuario que consumio.
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

   //cancelo si por error marque un usuario que consumio.
    @PostMapping("/cancelar-consumido/{id}")
    public String cancelarConsumido(@PathVariable("id") Integer idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva).orElse(null);

        if (reserva != null) {
            reserva.setEntregado(null);  // Restablecer el estado de consumo
            reservaRepository.save(reserva);  // Guardar los cambios en la base de datos
        }

        return "redirect:/reservas/reservas-dia-actual";  // Redirigir a la vista de reservas del día actual
    }


}
