package com.comedor.comedor.controller;


import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.repository.ReservaRepository;
import com.comedor.comedor.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;



    @GetMapping("/reporte")
    public String obtenerReservasFiltradas(
            @RequestParam(name = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(name = "apellido", required = false) String apellido,
            @RequestParam(name = "empresa", required = false) String empresa,
            @RequestParam(name = "cc", required = false) String cc,
            @RequestParam(name = "entregado", required = false) Boolean entregado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        List<String> empresas = usuarioRepository.findDistinctEmpresa();
        List<String> cecos = usuarioRepository.findDistinctCC();

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

        PageRequest pageRequest = PageRequest.of(page, 30); // Paginación de 30 elementos
        List<Reserva> reservasFiltradas = reservaRepository.findAll().stream()
                .filter(reserva -> (fechaInicio == null || !reserva.getMenu().getFechaMenu().isBefore(fechaInicio)))
                .filter(reserva -> (fechaFin == null || !reserva.getMenu().getFechaMenu().isAfter(fechaFin)))
                .filter(reserva -> (apellido == null || reserva.getUsuario().getApellido().toLowerCase().contains(apellido.toLowerCase())))
                .filter(reserva -> (empresa == null || reserva.getUsuario().getEmpresa().toLowerCase().contains(empresa.toLowerCase())))
                .filter(reserva -> (cc == null || reserva.getUsuario().getCc().toLowerCase().contains(cc.toLowerCase())))
                .filter(reserva -> (entregado == null || (entregado ? reserva.getEntregado() != null : reserva.getEntregado() == null)))
                .collect(Collectors.toList());

        long totalReservas = reservasFiltradas.size();
        long totalConsumidas = reservasFiltradas.stream().filter(reserva -> reserva.getEntregado() != null).count();
        long totalNoConsumidas = totalReservas - totalConsumidas;

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
        model.addAttribute("cc", cc);
        model.addAttribute("empresas",empresas);
        model.addAttribute("cecos",cecos);
        model.addAttribute("entregado", entregado);
        model.addAttribute("currentPage", page);
        //model.addAttribute("totalPages", reservasFiltradas.getTotalPages());

        return "reporte/reporte";
    }

    @GetMapping("/excel")
    public void exportarExcel(HttpServletResponse response,
                              @RequestParam(name = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                              @RequestParam(name = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                              @RequestParam(name = "apellido", required = false) String apellido,
                              @RequestParam(name = "empresa", required = false) String empresa,
                              @RequestParam(name = "cc", required = false) String cc,
                              @RequestParam(name = "entregado", required = false) Boolean entregado) throws IOException {

        List<Reserva> reservasFiltradas = reservaRepository.findAll().stream()
                .filter(reserva -> (fechaInicio == null || !reserva.getMenu().getFechaMenu().isBefore(fechaInicio)))
                .filter(reserva -> (fechaFin == null || !reserva.getMenu().getFechaMenu().isAfter(fechaFin)))
                .filter(reserva -> (apellido == null || reserva.getUsuario().getApellido().toLowerCase().contains(apellido.toLowerCase())))
                .filter(reserva -> (empresa == null || reserva.getUsuario().getEmpresa().toLowerCase().contains(empresa.toLowerCase())))
                .filter(reserva -> (cc == null || reserva.getUsuario().getCc().toLowerCase().contains(cc.toLowerCase())))
                .filter(reserva -> (entregado == null || (entregado ? reserva.getEntregado() != null : reserva.getEntregado() == null)))
                .collect(Collectors.toList());

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=ReporteReservas.xlsx");

        // Lógica para crear el archivo Excel con Apache POI
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reservas");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Apellido");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Tipo de Comida");
        header.createCell(3).setCellValue("Lugar de Consumo");
        header.createCell(4).setCellValue("Estado de Entrega");

        int rowIdx = 1;
        for (Reserva reserva : reservasFiltradas) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(reserva.getUsuario().getApellido());
            row.createCell(1).setCellValue(reserva.getUsuario().getNombre());
            row.createCell(2).setCellValue(reserva.getMenu().getComida().getTipo_comida());
            row.createCell(3).setCellValue(reserva.getMedio());
            row.createCell(4).setCellValue(reserva.getEntregado() != null ? "Entregado" : "No Entregado");
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }





}
