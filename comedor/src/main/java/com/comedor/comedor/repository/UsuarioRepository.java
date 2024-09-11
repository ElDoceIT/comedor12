package com.comedor.comedor.repository;

import com.comedor.comedor.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("SELECT u FROM Usuario u WHERE "
            + "(?1 IS NULL OR u.dni = ?1) "
            + "AND (?2 IS NULL OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', ?2, '%')))")
    Page<Usuario> buscarPorFiltros(Integer dni, String apellido, Pageable pageable);
}
