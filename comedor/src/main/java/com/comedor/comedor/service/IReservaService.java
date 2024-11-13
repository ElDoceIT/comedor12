package com.comedor.comedor.service;


import com.comedor.comedor.dto.ReservaForm;
import com.comedor.comedor.model.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IReservaService {
    Usuario obtenerUsuarioLogueado();
    //Reserva guardarReserva(ReservaForm reservaForm, Usuario usuario);
    List<Reserva> obtenerReservasSemanalesPorUsuario(Integer dni);
    void eliminarPorId(Integer idReserva);
    Reserva buscarPorId(Integer idReserva);
    void guardar(Reserva reserva);
    boolean existeReservaParaUsuarioYFecha(Usuario usuario, LocalDate fechaMenu);
    List<Reserva> obtenerReservasPorUsuario(Usuario usuario);
    List<Reserva> obtenerReservasForzadasDelDia(LocalDate fecha);
    Reserva buscarReservaPorUsuarioYMenu(Integer dni, Menu menu);

    List<Reserva> obtenerReservasEntreFechasPorUsuario(Integer dni, LocalDate fechaInicio, LocalDate fechaFin);


}
