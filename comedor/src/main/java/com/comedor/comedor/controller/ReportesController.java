package com.comedor.comedor.controller;


import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    ReservaRepository reservaRepository;

    @GetMapping("/reporte")
    public String obtenerReservasFiltradas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String empresa,
            Model model) {

        // Mapas para las descripciones
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

        // Filtros
        List<Reserva> reservasFiltradas = reservaRepository.findAll().stream()
                .filter(reserva -> (fechaInicio == null || !reserva.getMenu().getFechaMenu().isBefore(fechaInicio)))
                .filter(reserva -> (fechaFin == null || !reserva.getMenu().getFechaMenu().isAfter(fechaFin)))
                .filter(reserva -> (apellido == null || reserva.getUsuario().getApellido().toLowerCase().contains(apellido.toLowerCase())))
                .filter(reserva -> (empresa == null || reserva.getUsuario().getEmpresa().toLowerCase().contains(empresa.toLowerCase())))
                .collect(Collectors.toList());

        // Contar el total de reservas y las no consumidas
        long totalReservas = reservasFiltradas.size();
        long totalConsumidas = reservasFiltradas.stream().filter(reserva -> reserva.getEntregado() != null).count();
        long totalNoConsumidas = totalReservas - totalConsumidas;

        // Añadir datos al modelo
        model.addAttribute("reservasFiltradas", reservasFiltradas);
        model.addAttribute("tipoComidaDescripcion", tipoComidaDescripcion);
        model.addAttribute("medioDescripcion", medioDescripcion);
        model.addAttribute("totalReservas", totalReservas);
        model.addAttribute("totalConsumidas", totalConsumidas);
        model.addAttribute("totalNoConsumidas", totalNoConsumidas);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("apellido", apellido);
        model.addAttribute("empresa", empresa);

        return "reporte/reporte";  // Nombre de la vista
    }


}