package com.comedor.comedor.service;


import com.comedor.comedor.model.Menu;

import java.time.LocalDate;
import java.util.List;

public interface IMenuService {

    void guardar(Menu menu);

    List<Menu> buscarTodas();

    Menu buscarPorId(Integer id_menu);
    List<Menu> obtenerMenusNoProcesados();
    void marcarMenusComoProcesados();
    void eliminarPorId(Integer id);
    //List<Menu> obtenerMenuesDesdeFecha(LocalDate fecha);
}
