package com.comedor.comedor.service;

import com.comedor.comedor.model.Comida;

import java.util.List;

public interface IComidaService {
    void guardar(Comida comida);
    List<Comida> buscarTodas();
    Comida buscarPorId(Integer id_comida);
}
