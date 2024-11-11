package com.comedor.comedor.service;

import com.comedor.comedor.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IUsuarioService {

    List<Usuario> buscarTodas();
    Page<Usuario>buscarPorFiltros(Usuario usuarioExample, Pageable pageable);
    Page<Usuario> buscarTodas(Pageable pageable);
    Usuario obtenerPorDni(Integer dni);
    Usuario obtenerPorId(Integer id);
    void migratePasswords();
    void actualizarUsuario(Usuario usuario);
    Page<Usuario> buscarUsuariosConFiltros(Integer dni, String apellido, String empresa, Pageable pageable);
}
