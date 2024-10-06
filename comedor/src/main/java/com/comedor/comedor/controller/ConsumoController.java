package com.comedor.comedor.controller;

import com.comedor.comedor.model.Consumo;

import com.comedor.comedor.model.Producto;
import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.ConsumoRepository;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.repository.UsuarioRepository;
import com.comedor.comedor.service.IConsumoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private IConsumoService consumoService;


    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    //ver todos los consumos de todos los usuarios.
/*@GetMapping("/ver")
    public String consumosVer(Model model) {
        //model.addAttribute("consumos", consumoRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        model.addAttribute("consumos", consumoRepository.findAllByOrderByFechaDesc());
        return "consumos/consumos_ver";

    }*/
    @GetMapping("/ver")
    public String consumosVer(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 3;  // Cambia el tamaño de página según tus necesidades
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("fecha").descending());
        Page<Consumo> consumosPage = consumoRepository.findAllByOrderByFechaDesc(pageable);

        model.addAttribute("consumos", consumosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", consumosPage.getTotalPages());

        return "consumos/consumos_ver";
    }

    //vista para asignar productos a los usuarios, ademas de la cantidad y fecha.
    @GetMapping("/asignar")
    public String consumosAsignar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "consumos/consumos_asignar";

    }
    //en consumos_asignar, filtrar por producto
    @GetMapping("/productos/buscar")
    @ResponseBody
    public List<Producto> buscarProductos(@RequestParam("query") String query) {
        return productoRepository.findByDescripcionContainingIgnoreCase(query);
    }
    //en comsumos_asignar, filtrar por usuarios
    @GetMapping("/usuarios/buscar")
    @ResponseBody
    public List<Usuario> buscarUsuarios(@RequestParam String query) {
        return usuarioRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(query, query);
    }

    @GetMapping("/search")
    public String buscarConsumos(
            @RequestParam(name = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(name = "usuario", required = false) String usuario,
            Model model) {

        // Lógica para filtrar los consumos según los parámetros recibidos
        List<Consumo> consumos = consumoService.buscarConsumos(fechaInicio, fechaFin, usuario);

        // Añadir los filtros actuales para poder mostrarlos en la vista
        model.addAttribute("consumos", consumos);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("usuario", usuario);

        return "consumos/consumos_ver";  // Asegúrate de que esta es la vista que estás utilizando
    }


    //guarda consumos_asignar
    @PostMapping("/save")
    public String guardarConsumo(Consumo consumo) {
        consumoRepository.save(consumo);
        return "redirect:/consumos/ver";
    }

    @GetMapping("/editar/{id}")
    public String editarConsumo(@PathVariable("id") int idConsumo, Model model) {
        Consumo consumo = consumoRepository.findById(idConsumo).get();
        model.addAttribute("consumo",consumo);
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "consumos/consumos_editar";
    }

    @GetMapping("/delete/{id}")
    public String eliminarConsumo(@PathVariable("id") int IdConsumo) {
        consumoRepository.deleteById(IdConsumo);
        return "redirect:/consumos/ver";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel(@RequestParam(required = false) String usuario,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) throws IOException {
        // Lógica para obtener los consumos filtrados
        List<Consumo> consumos = consumoService.buscarConsumos(fechaInicio, fechaFin,usuario);

        // Generar el archivo Excel
        ByteArrayInputStream in = exportToExcel(consumos);

        // Preparar la respuesta para descargar el archivo Excel
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=consumos_filtrados.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(in.readAllBytes());
    }


    private ByteArrayInputStream exportToExcel(List<Consumo> consumos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Consumos");

            // Crear el encabezado
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Fecha");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Apellido");
            header.createCell(3).setCellValue("Producto");
            header.createCell(4).setCellValue("Cantidad");

            // Rellenar los datos desde la base de datos
            int rowIdx = 1;
            for (Consumo consumo : consumos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(consumo.getFecha().toString());
                row.createCell(1).setCellValue(consumo.getUsuario().getNombre());
                row.createCell(2).setCellValue(consumo.getUsuario().getApellido());
                row.createCell(3).setCellValue(consumo.getProducto().getDescripcion());
                row.createCell(4).setCellValue(consumo.getCantidad());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
