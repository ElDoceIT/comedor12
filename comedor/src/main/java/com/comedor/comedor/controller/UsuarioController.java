package com.comedor.comedor.controller;

import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.UsuarioRepository;
import com.comedor.comedor.service.IUsuarioService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  /*  public void migratePasswords() {
        List<Usuario> users = usuarioRepository.findAll();
        for (Usuario user : users) {
            if (!user.getPass().startsWith("$2a$")) {
                String encodedPassword = passwordEncoder.encode(user.getPass());
                user.setPass(encodedPassword);
                usuarioRepository.save(user);
            }
        }
    }*/

    @GetMapping("/new")
    public String nuevoUsuarios(Model model) {
        List<String> grupos = usuarioRepository.findDistinctGrupos();
        List<String> empresas = usuarioRepository.findDistinctEmpresa();
        List<String> estados = usuarioRepository.findDistinctEstado();
        List<String> cecos = usuarioRepository.findDistinctCC();

        // Convertir estados 1/0 a Activo/Inactivo
        List<String> estadosProcesados = estados.stream()
                .map(estado -> estado.equals("1") ? "Activo" : "Inactivo")
                .collect(Collectors.toList());

        model.addAttribute("grupos",grupos);
        model.addAttribute("empresas",empresas);
        model.addAttribute("estados",estados);
        model.addAttribute("cecos",cecos);
        return "usuarios/usuarios_new";
    }

   /* @GetMapping("/ver")
    public String consumosVer(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios/usuarios_ver";
    }*/
   @GetMapping("/ver")
   public String consumosVer(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "12") int size,
           @RequestParam(required = false) Integer dni,
           @RequestParam(required = false) String apellido,
           @RequestParam(required = false) String empresa,
           Model model) {

       Pageable pageable = PageRequest.of(page, size);
       Page<Usuario> usuarios = usuarioService.buscarUsuariosConFiltros(dni, apellido, empresa, pageable);

       model.addAttribute("usuarios", usuarios);
       model.addAttribute("currentPage", page);
       model.addAttribute("totalPages", usuarios.getTotalPages());
       model.addAttribute("dni", dni);
       model.addAttribute("apellido", apellido);
       model.addAttribute("empresa", empresa);

       return "usuarios/usuarios_ver";
   }

    @PostMapping("/save")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        // Verificar si el DNI ya existe
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            // Agregar un mensaje de error al RedirectAttributes
            redirectAttributes.addFlashAttribute("error", "El usuario con DNI " + usuario.getDni() + " ya existe.");
            return "redirect:/usuarios/new"; // Redirigir al formulario de creación
        }

        // Verificar si la contraseña no está codificada
        if (!usuario.getPass().startsWith("$2a$")) {
            // Codificar la contraseña con BCrypt
            String encodedPassword = passwordEncoder.encode(usuario.getPass());
            usuario.setPass(encodedPassword);
        }


        // Guardar el usuario
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success", "Usuario guardado exitosamente.");
        return "redirect:/usuarios/ver"; // Redirigir a la lista de usuarios
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
   @GetMapping("/migrate-passwords")
    public String migratePasswords() {
        usuarioService.migratePasswords();
        return "Migración completada";
    }

    @GetMapping("/cambiar-clave")
    public String mostrarFormularioCambioClave() {
        return "usuarios/usuarios_cambiar_pass";
    }
    //cambio de clave por el mismo usuario, sabiendo la suya anterior.
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

    // Muestra el formulario de cambio de contraseña para rrhh
    @GetMapping("/cambiar-clave-rrhh")
    public String mostrarCambioClave(@RequestParam("dni") Integer dni, Model model) {
        model.addAttribute("dni", dni); // Agrega el DNI al modelo
        return "usuarios/usuarios_cambiar_pass_rrhh"; // Devuelve la vista
    }

    // Procesa el cambio de contraseña
    @PostMapping("/cambiar-clave-rrhh")
    public String cambiarClaveRRHH (@RequestParam("dni") Integer dni,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {

       // Busca el usuario por su DNI
        Usuario usuario = usuarioService.obtenerPorDni(dni);

         // Verificar si el usuario existe
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "usuarios/usuarios_cambiar_pass_rrhh";
        }
        model.addAttribute("dni", dni);

        // Verificar si la nueva contraseña y la confirmación coinciden
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas nuevas no coinciden");
            return "usuarios/usuarios_cambiar_pass_rrhh";
        }


        // Encriptar la nueva contraseña usando BCrypt
        String encodedPassword = passwordEncoder.encode(newPassword);
        usuario.setPass(encodedPassword);  // Actualiza la contraseña en el usuario
        usuarioService.actualizarUsuario(usuario);  // Guarda los cambios en la base de datos
        // Mostrar mensaje de éxito
        model.addAttribute("success", "La contraseña se cambió exitosamente");
        return "usuarios/usuarios_cambiar_pass_rrhh";
    }

}
