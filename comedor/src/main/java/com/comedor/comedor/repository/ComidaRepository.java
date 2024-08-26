package com.comedor.comedor.repository;

import com.comedor.comedor.model.Comida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComidaRepository  extends JpaRepository<Comida, Integer> {
}
