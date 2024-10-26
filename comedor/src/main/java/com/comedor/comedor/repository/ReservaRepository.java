package com.comedor.comedor.repository;

import com.comedor.comedor.dto.ReservaForm;
import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    @Query("SELECT r FROM Reserva r WHERE r.usuario.dni = :dni AND r.menu.fechaMenu BETWEEN :inicioSemana AND :finSemana")
    List<Reserva> findReservasSemanalesPorUsuario(@Param("dni") Integer dni, @Param("inicioSemana") LocalDate inicioSemana, @Param("finSemana") LocalDate finSemana);

    //boolean existsByUsuarioIdAndMenuFecha(Integer idUsuario, LocalDate fechaMenu);
    boolean existsByUsuarioAndMenu_FechaMenu(Usuario usuario, LocalDate fechaMenu);
    List<Reserva> findByUsuario(Usuario usuario);
    List<Reserva> findByMenu_FechaMenuBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Reserva r WHERE r.usuario.dni = :dni AND r.menu.fechaMenu BETWEEN :fechaInicio AND :fechaFin")
    List<Reserva> findReservasByUsuarioAndFechaMenuBetween(
            @Param("dni") Integer dni,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

}
