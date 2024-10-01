package com.comedor.comedor.controller;

import com.comedor.comedor.model.Consumo;

import com.comedor.comedor.repository.ConsumoRepository;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
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
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoRepository consumoRepository;


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


    @GetMapping("/ver")
    public String consumosVer(Model model) {
        //model.addAttribute("consumos", consumoRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        model.addAttribute("consumos", consumoRepository.findAllByOrderByFechaDesc());
        return "consumos/consumos_ver";

    }


    @GetMapping("/asignar")
    public String consumosAsignar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "consumos/consumos_asignar";

    }

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
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        // Obtener todos los consumos desde la base de datos
        List<Consumo> consumos =consumoRepository.findAll();

        // Generar el archivo Excel
        ByteArrayInputStream in = exportToExcel(consumos);

        // Preparar la respuesta para descargar el archivo Excel
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=consumos.xlsx");

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
