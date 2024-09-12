package com.comedor.comedor.repository;

import com.comedor.comedor.model.Comida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ComidaRepository  extends JpaRepository<Comida, Integer> {
    Page<Comida> findByPrincipalContainingIgnoreCase(String principal, Pageable pageable);
}
