package com.comedor.comedor.service.db;

import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.UsuarioRepository;
import com.comedor.comedor.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceJPA implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> buscarTodas() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> buscarPorFiltros(Usuario usuarioExample, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                // Comparación exacta para DNI (número entero)
                .withMatcher("dni", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("apellido", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<Usuario> example = Example.of(usuarioExample, matcher);
        return usuarioRepository.findAll(example, pageable);
    }

    public Page<Usuario> buscarTodas(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }


}
