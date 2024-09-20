package com.comedor.comedor.controller;

import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.UsuarioRepository;
import com.comedor.comedor.service.IUsuarioService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioService usuarioService;

    @PostConstruct
    public void migratePasswordsOnStartup() {
        migratePasswords();
    }

    //este metodo se ejecuta automaticamente cada vez que se inicia la aplicacion, una vez migrados todos los user, lo doy de baja.
    public void migratePasswords() {
        List<Usuario> users = usuarioRepository.findAll();
        for (Usuario user : users) {
            if (!user.getPass().startsWith("$2a$")) {
                String encodedPassword = passwordEncoder.encode(user.getPass());
                user.setPass(encodedPassword);
                usuarioRepository.save(user);
            }
        }
    }

    @GetMapping("/new")
    public String nuevoUsuarios(Model model) {
        List<String> grupos = usuarioRepository.findDistinctGrupos();
        List<String> empresas = usuarioRepository.findDistinctEmpresa();
        List<String> estados = usuarioRepository.findDistinctEstado();
        List<String> cecos = usuarioRepository.findDistinctCC();

        model.addAttribute("grupos",grupos);
        model.addAttribute("empresas",empresas);
        model.addAttribute("estados",estados);
        model.addAttribute("cecos",cecos);
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
        List<String> grupos = usuarioRepository.findDistinctGrupos();
        List<String> empresas = usuarioRepository.findDistinctEmpresa();
        List<String> estados = usuarioRepository.findDistinctEstado();
        List<String> cecos = usuarioRepository.findDistinctCC();
        model.addAttribute("usuario",usuario);
        model.addAttribute("grupos",grupos);
        model.addAttribute("empresas",empresas);
        model.addAttribute("estados",estados);
        model.addAttribute("cecos",cecos);
        return "usuarios/usuarios_edit";
    }

    @GetMapping("/search")
    public String listarUsuarios(
            @RequestParam(value = "dni", required = false)  String dniParam,
            @RequestParam(value = "apellido", required = false) String apellido,
            @PageableDefault(size = 10) Pageable pageable, Model model) {

        Page<Usuario> usuarios;
        Integer dni = null;
        // Convertir dniParam a Integer si es posible
        if (dniParam != null && !dniParam.isEmpty()) {
            try {
                dni = Integer.parseInt(dniParam);
            } catch (NumberFormatException e) {
                // Ignorar si no es un número válido
            }
        }

        // Usar el método de búsqueda personalizada
        usuarios = usuarioRepository.buscarPorFiltros(dni, apellido, pageable);



            // Añadir atributos al modelo para ser usados en la vista
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("dni", dni);
            model.addAttribute("apellido", apellido);

            return "usuarios/usuarios_ver";
    }
   //una vez migrados todos los usuarios, activo para encriptar claves por la url
   /* @GetMapping("/migrate-passwords")
    public String migratePasswords() {
        usuarioService.migratePasswords();
        return "Migración completada";
    }*/

    @GetMapping("/cambiar-clave")
    public String mostrarFormularioCambioClave() {
        return "usuarios/usuarios_cambiar_pass";
    }

    @PostMapping("/cambiar-clave")
    public String cambiarClave(
                               @RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {

        // Obtén el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String dni = auth.getName(); // Asumiendo que el dni es el username en el contexto de Spring Security

        // Busca al usuario autenticado en la base de datos usando su dni
        Usuario usuario = usuarioService.obtenerPorDni(Integer.parseInt(dni));

        // Verificar si el usuario existe
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "usuarios/usuarios_cambiar_pass";
        }

        // Verificar si la contraseña actual es correcta
        if (!passwordEncoder.matches(currentPassword, usuario.getPass())) {
            model.addAttribute("error", "La contraseña actual es incorrecta");
            return "usuarios/usuarios_cambiar_pass";
        }

        // Verificar si la nueva contraseña y la confirmación coinciden
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas nuevas no coinciden");
            return "usuarios/usuarios_cambiar_pass";
        }

        // Encriptar la nueva contraseña usando BCrypt
        String encodedPassword = passwordEncoder.encode(newPassword);
        usuario.setPass(encodedPassword);  // Actualiza la contraseña en el usuario
        usuarioService.actualizarUsuario(usuario);  // Guarda los cambios en la base de datos

        // Mostrar mensaje de éxito
        model.addAttribute("success", "La contraseña se cambió exitosamente");
        return "home";
    }
}
