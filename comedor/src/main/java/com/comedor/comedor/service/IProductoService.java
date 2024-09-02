package com.comedor.comedor.service;


import com.comedor.comedor.model.Producto;

import java.util.List;

public interface IProductoService {

    void guardar(Producto producto);
    List<Producto> buscarTodas();
}
