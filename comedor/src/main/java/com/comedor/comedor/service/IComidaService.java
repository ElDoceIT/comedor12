package com.comedor.comedor.service;

import com.comedor.comedor.model.Comida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IComidaService {
    void guardar(Comida comida);
    List<Comida> buscarTodas();
    Comida buscarPorId(Integer id_comida);
    Page<Comida> buscarPorPrincipal(String principal, Pageable pageable);
    Page<Comida> buscarTodos (Pageable pageable);
}
