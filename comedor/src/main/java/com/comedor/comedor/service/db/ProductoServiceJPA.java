package com.comedor.comedor.service.db;
import com.comedor.comedor.model.Producto;
import com.comedor.comedor.repository.ProductoRepository;
import com.comedor.comedor.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<Producto> buscarByExample(Example<Producto> example) {
        return productoRepository.findAll(example);
    }

    @Override
    public Page<Producto> buscarTodas(Pageable page) {
        return productoRepository.findAll(page);
    }

    @Override
    public Page<Producto> buscarPorNombre(Producto example, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("descripcion", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<Producto> productoExample = Example.of(example, matcher);
        return productoRepository.findAll(productoExample, pageable);
    }


}
