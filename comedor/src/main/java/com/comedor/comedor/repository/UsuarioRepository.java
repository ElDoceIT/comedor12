package com.comedor.comedor.repository;

import com.comedor.comedor.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByDni(Integer dni);

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    @Query("SELECT u FROM Usuario u WHERE "
            + "(?1 IS NULL OR u.dni = ?1) "
            + "AND (?2 IS NULL OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', ?2, '%')))")
    Page<Usuario> buscarPorFiltros(Integer dni, String apellido, Pageable pageable);

    @Query("SELECT DISTINCT u.grupo FROM Usuario u")
    List<String> findDistinctGrupos();

    @Query("SELECT DISTINCT u.cc FROM Usuario u")
    List<String> findDistinctCC();

    @Query("SELECT DISTINCT u.empresa FROM Usuario u")
    List<String> findDistinctEmpresa();

    @Query("SELECT DISTINCT u.estado FROM Usuario u")
    List<String> findDistinctEstado();
}
