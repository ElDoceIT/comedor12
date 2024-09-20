package com.comedor.comedor.repository;

import com.comedor.comedor.dto.ReservaForm;
import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

}
