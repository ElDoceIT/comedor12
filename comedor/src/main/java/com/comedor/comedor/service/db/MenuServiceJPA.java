package com.comedor.comedor.service.db;


import com.comedor.comedor.model.Menu;
import com.comedor.comedor.repository.MenuRepository;
import com.comedor.comedor.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
