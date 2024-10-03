package com.comedor.comedor.service.db;

import com.comedor.comedor.model.Consumo;
import com.comedor.comedor.repository.ConsumoRepository;
import com.comedor.comedor.service.IConsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConsumoServiceJPA implements IConsumoService {

    @Autowired
    private ConsumoRepository consumoRepository;

    @Override
    public List<Consumo> buscarTodas() {
        return consumoRepository.findAll();
    }

    @Override
    public void guardar(Consumo consumo) {
        consumoRepository.save(consumo);

    }

    @Override
    public List<Consumo> buscarConsumos(LocalDate fechaInicio, LocalDate fechaFin, String usuario) {
        return consumoRepository.buscarConsumosFiltrados(fechaInicio, fechaFin, usuario);
    }
}
