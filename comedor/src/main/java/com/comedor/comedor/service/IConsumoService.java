package com.comedor.comedor.service;

import com.comedor.comedor.model.Consumo;

import java.time.LocalDate;
import java.util.List;

public interface IConsumoService {
    List<Consumo> buscarTodas();
    void guardar(Consumo consumo);
    List<Consumo> buscarConsumos(LocalDate fechaInicio, LocalDate fechaFin, String usuario);
}
