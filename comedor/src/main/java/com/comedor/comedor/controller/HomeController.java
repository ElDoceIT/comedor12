package com.comedor.comedor.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {



    @GetMapping("/")
    public String Home(){
        return "home";
    }

    @GetMapping("/fer")
    public String HomeFer(){
        return "fer";
    }

    @GetMapping("/waldo")
    public String HomeWaldo(){
        return "waldo";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "DNI o contraseña incorrectos. Inténtalo de nuevo.");
        }
        return "login";  // Nombre de la vista del formulario de login
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        SecurityContextLogoutHandler logoutHandler =
                new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        return "redirect:/login";
    }





}
