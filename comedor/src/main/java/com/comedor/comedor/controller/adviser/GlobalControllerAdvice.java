package com.comedor.comedor.controller.adviser;

import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private IUsuarioService usuarioService;

    @ModelAttribute("nombreCompleto")
    public String agregarNombreCompleto(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            Integer dniUsuario = Integer.parseInt(userDetails.getUsername());
            Usuario usuario = usuarioService.obtenerPorDni(dniUsuario);
            return usuario.getNombre() + " " + usuario.getApellido();
        }
        return "";
    }

}
