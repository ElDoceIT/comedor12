package com.comedor.comedor.service.db;
import com.comedor.comedor.model.Producto;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceJPA implements IProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public List<Producto> buscarTodas() {
        return productoRepository.findAll();
    }


}
