package com.comedor.comedor.service;


import com.comedor.comedor.model.Menu;

import java.util.List;

public interface IMenuService {

    void guardar(Menu menu);

    List<Menu> buscarTodas();

    Menu buscarPorId(Integer id_menu);
}