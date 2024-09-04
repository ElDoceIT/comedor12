package com.comedor.comedor.service;


import com.comedor.comedor.model.Producto;
import org.springframework.data.domain.Example;

import java.util.List;

public interface IProductoService {

    void guardar(Producto producto);
    List<Producto> buscarTodas();
    List<Producto> buscarByExample(Example<Producto> example);
}
