package com.comedor.comedor.service.db;

import com.comedor.comedor.model.Comida;
import com.comedor.comedor.repository.ComidaRepository;
import com.comedor.comedor.service.IComidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComidaServiceJPA implements IComidaService {

    @Autowired
    private ComidaRepository comidaRepository;

    @Override
    public void guardar(Comida comida) {
        comidaRepository.save(comida);

    }

    @Override
    public List<Comida> buscarTodas() {
        return comidaRepository.findAll();
    }

    @Override
    public Comida buscarPorId(Integer id) {
        Optional<Comida> comi= comidaRepository.findById(id);
        if(comi.isPresent()){
            return comi.get();
        }
        return null;
    }
}
