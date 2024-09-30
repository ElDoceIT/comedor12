package com.comedor.comedor.service.db;


import com.comedor.comedor.model.Menu;
import com.comedor.comedor.repository.MenuRepository;
import com.comedor.comedor.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class MenuServiceJPA implements IMenuService {

    @Autowired
    private MenuRepository menuRepository;


    @Override
    public void guardar(Menu menu) {
        menuRepository.save(menu);
    }

    @Override
    public List<Menu> buscarTodas() {
        return List.of();
    }

    @Override
    public Menu buscarPorId(Integer id_menu) {
        return null;
    }

    @Override
    public List<Menu> obtenerMenusNoProcesados() {
        return menuRepository.findByPublicar(0);
    }

    @Override
    public void marcarMenusComoProcesados() {
        List<Menu> menus = menuRepository.findByPublicar(0);
        for (Menu menu : menus) {
            menu.setPublicar(1);  // Marcar como procesado
            menuRepository.save(menu);
        }
    }

    @Override
    public void eliminarPorId(Integer id) {
        menuRepository.deleteById(id);
    }

    @Override
    public List<Menu> getMenusSemana(LocalDate lunes, LocalDate viernes) {
        return menuRepository.findMenusByFechaMenuBetween(lunes, viernes);
    }


}
