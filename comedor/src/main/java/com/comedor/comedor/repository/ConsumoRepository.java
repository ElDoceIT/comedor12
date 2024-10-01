package com.comedor.comedor.repository;

import com.comedor.comedor.model.Consumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumoRepository extends JpaRepository<Consumo, Integer> {
    List<Consumo> findAllByOrderByFechaDesc();
}
