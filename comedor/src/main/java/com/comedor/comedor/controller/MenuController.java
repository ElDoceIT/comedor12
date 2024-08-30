package com.comedor.comedor.controller;

import com.comedor.comedor.model.Menu;
import com.comedor.comedor.repository.MenuRepository;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private MenuRepository menuRepository;

    @GetMapping("/new")
    public String guardarMenu(){
        return "menu_new";
    }

    @PostMapping("/save")
    public String guardarMenu(Menu menu){
        menuRepository.save(menu);
        return "redirect:/menu/ver";
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

}
