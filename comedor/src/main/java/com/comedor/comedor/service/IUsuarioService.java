package com.comedor.comedor.service;

import com.comedor.comedor.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IUsuarioService {

    List<Usuario> buscarTodas();
}
