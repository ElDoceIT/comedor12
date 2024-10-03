package com.comedor.comedor.repository;

import com.comedor.comedor.model.Consumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ConsumoRepository extends JpaRepository<Consumo, Integer> {
    List<Consumo> findAllByOrderByFechaDesc();

    @Query("SELECT c FROM Consumo c WHERE " +
            "(:fechaInicio IS NULL OR c.fecha >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR c.fecha <= :fechaFin) AND " +
            "(:usuario IS NULL OR (c.usuario.nombre LIKE %:usuario% OR c.usuario.apellido LIKE %:usuario%))")
    List<Consumo> buscarConsumosFiltrados(@Param("fechaInicio") LocalDate fechaInicio,
                                          @Param("fechaFin") LocalDate fechaFin,
                                          @Param("usuario") String usuario);
}
