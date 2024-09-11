package com.comedor.comedor.controller;

import com.comedor.comedor.model.Producto;
import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.UsuarioRepository;
import com.comedor.comedor.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/new")
    public String nuevoUsuarios(){
        return "usuarios/usuarios_new";
    }

    @GetMapping("/ver")
    public String consumosVer(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios/usuarios_ver";
    }

    @PostMapping("/save")
    public String guardarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
        return "redirect:/usuarios/ver";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable("id") int id_usuario, Model model) {
        Usuario usuario= usuarioRepository.findById(id_usuario).get();
        model.addAttribute("usuario",usuario);
        return "usuarios/usuarios_edit";
    }

    @GetMapping("/search")
    public String listarUsuarios(
            @RequestParam(value = "dni", required = false) Integer dni,
            @RequestParam(value = "apellido", required = false) String apellido,
            @PageableDefault(size = 10) Pageable pageable, Model model) {

        Page<Usuario> usuarios;
        //dni = null;

            if (dni != null || (apellido != null && !apellido.isEmpty())) {
                // Crear un objeto Usuario para el ejemplo de búsqueda
                Usuario usuarioExample = new Usuario();

                if (dni != null) {
                    usuarioExample.setDni(dni);
                }

                if (apellido != null && !apellido.isEmpty()) {
                    usuarioExample.setApellido(apellido);
                }

                // Buscar usuarios por los filtros de dni y/o apellido
                usuarios = usuarioService.buscarPorFiltros(usuarioExample, pageable);
            } else {
                usuarios = usuarioService.buscarTodas(pageable);
            }

            // Añadir atributos al modelo para ser usados en la vista
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("dni", dni);
            model.addAttribute("apellido", apellido);

            return "usuarios/usuarios_ver";
    }
}
