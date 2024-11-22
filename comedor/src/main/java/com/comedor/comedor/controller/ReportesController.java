package com.comedor.comedor.controller;


import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.ReservaRepository;
import com.comedor.comedor.repository.UsuarioRepository;
import com.comedor.comedor.service.db.UsuarioServiceJPA;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    private UsuarioServiceJPA usuarioService;



    @GetMapping("/reporte")
    public String obtenerReservasFiltradas(
            @RequestParam(name = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(name = "apellido", required = false) String apellido,
            @RequestParam(name = "empresa", required = false) String empresa,
            @RequestParam(name = "cc", required = false) String cc,
            @RequestParam(name = "entregado", required = false) Boolean entregado,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model,
            Authentication authentication) {

        // Obtener la empresa del usuario autenticado
        Usuario usuarioAutenticado = usuarioService.obtenerPorDni(Integer.parseInt(authentication.getName()));
        String empresaUsuario = usuarioAutenticado.getEmpresa();

        // Paginación de 10 elementos por página
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "menu.fechaMenu"));

        // Consulta con filtros paginados
        Page<Reserva> reservasFiltradas = reservaRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (fechaInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("menu").get("fechaMenu"), fechaInicio));
            }
            if (fechaFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("menu").get("fechaMenu"), fechaFin));
            }
            if (apellido != null && !apellido.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("usuario").get("apellido")),
                        "%" + apellido.toLowerCase() + "%"
                ));
            }
            if (empresa != null && !empresa.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("usuario").get("empresa")),
                        "%" + empresa.toLowerCase() + "%"
                ));
            } else {
                // Si el usuario no es administrador, filtrar por su empresa
                String rolUsuario = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(role -> role.equals("Admin"))
                        .findFirst()
                        .orElse(null);

                if (rolUsuario == null) { // No es admin, aplicar filtro por empresa del usuario autenticado
                    predicates.add(criteriaBuilder.equal(root.get("usuario").get("empresa"), empresaUsuario));
                }
            }

            if (cc != null && !cc.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("usuario").get("cc")),
                        "%" + cc.toLowerCase() + "%"
                ));
            }
            if (entregado != null) {
                if (entregado) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("entregado")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("entregado")));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);

        // Resumen de totales
        long totalReservas = reservasFiltradas.getTotalElements();
        long totalConsumidas = reservasFiltradas.stream().filter(reserva -> reserva.getEntregado() != null).count();
        long totalNoConsumidas = totalReservas - totalConsumidas;

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
        model.addAttribute("empresas", empresas);
        model.addAttribute("cecos", cecos);
        model.addAttribute("entregado", entregado);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservasFiltradas.getTotalPages());

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

        // Crear archivo Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reservas");

        // Encabezados
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("FechaMenu");
        header.createCell(1).setCellValue("Apellido");
        header.createCell(2).setCellValue("Nombre");
        header.createCell(3).setCellValue("CC");
        header.createCell(4).setCellValue("Empresa");
        header.createCell(5).setCellValue("Tipo de Comida");
        header.createCell(6).setCellValue("Lugar de Consumo");
        header.createCell(7).setCellValue("Estado de Entrega");

        // Mapeo para convertir valores numéricos en descripciones
        Map<Integer, String> tipoComidaDescripcion = Map.of(
                1, "Principal",
                2, "Light",
                3, "Celiaco",
                4, "Fruta"
        );

        Map<Integer, String> medioDescripcion = Map.of(
                1, "Comedor",
                2, "Vianda",
                3, "Retira"
        );

        // Datos
        int rowIdx = 1;
        for (Reserva reserva : reservasFiltradas) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(reserva.getMenu().getFechaMenu().toString());
            row.createCell(1).setCellValue(reserva.getUsuario().getApellido());
            row.createCell(2).setCellValue(reserva.getUsuario().getNombre());
            row.createCell(3).setCellValue(reserva.getUsuario().getCc());
            row.createCell(4).setCellValue(reserva.getUsuario().getEmpresa());

            // Mapea el tipo de comida y el medio de consumo a su descripción
            String tipoComida = tipoComidaDescripcion.getOrDefault(reserva.getMenu().getComida().getTipo_comida(), "Desconocido");
            String medioConsumo = medioDescripcion.getOrDefault(reserva.getMedio(), "Desconocido");

            row.createCell(5).setCellValue(tipoComida);
            row.createCell(6).setCellValue(medioConsumo);
            row.createCell(7).setCellValue(reserva.getEntregado() != null ? "Entregado" : "No Entregado");
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
