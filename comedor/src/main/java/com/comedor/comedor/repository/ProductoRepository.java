package com.comedor.comedor.repository;

import com.comedor.comedor.model.Producto;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByDescripcionContainingIgnoreCase(String descripcion);

}
