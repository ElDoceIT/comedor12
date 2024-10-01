package com.comedor.comedor.service;


import com.comedor.comedor.dto.ReservaForm;
import com.comedor.comedor.model.Comida;
import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.model.Usuario;

import java.util.List;

public interface IReservaService {
    Usuario obtenerUsuarioLogueado();
    Reserva guardarReserva(ReservaForm reservaForm, Usuario usuario);
    List<Reserva> obtenerReservasSemanalesPorUsuario(Integer dni);
    void eliminarPorId(Integer idReserva);
    Reserva buscarPorId(Integer idReserva);



}
