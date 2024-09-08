package com.comedor.comedor.service;

import com.comedor.comedor.model.Consumo;

import java.util.List;

public interface IConsumoService {
    List<Consumo> buscarTodas();
    void guardar(Consumo consumo);
}
