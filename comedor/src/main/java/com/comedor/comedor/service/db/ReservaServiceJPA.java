package com.comedor.comedor.service.db;

import com.comedor.comedor.dto.ReservaForm;
import com.comedor.comedor.model.Menu;
import com.comedor.comedor.model.Reserva;
import com.comedor.comedor.model.Usuario;
import com.comedor.comedor.repository.MenuRepository;
import com.comedor.comedor.repository.ReservaRepository;
import com.comedor.comedor.repository.UsuarioRepository;
import com.comedor.comedor.service.IReservaService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceJPA implements IReservaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private MenuRepository menuRepository;


    @Override
    public Usuario obtenerUsuarioLogueado() {
        return null;
    }

   /* @Override
    public Reserva guardarReserva(ReservaForm reservaForm, Usuario usuario) {
        Reserva reserva = new Reserva();
        reserva.setF_reserva(reservaForm.getF_reserva());
        reserva.setMenu(reservaForm.getMenu());
        reserva.setUsuario(usuario);

        // Medio de comida es un número
        reserva.setMedio(reservaForm.getMedio());  // Esto será un int (1, 2 o 3)

        return reservaRepository.save(reserva);
    }*/

    @Override
    public List<Reserva> obtenerReservasSemanalesPorUsuario(Integer dni) {
        LocalDate inicioSemana = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate finSemana = LocalDate.now().with(DayOfWeek.SUNDAY);
        return reservaRepository.findReservasSemanalesPorUsuario(dni, inicioSemana, finSemana);
    }

    @Override
    public void eliminarPorId(Integer idReserva) {
        reservaRepository.deleteById(idReserva);
    }

    @Override
    public Reserva buscarPorId(Integer idReserva) {
        Optional<Reserva> resep= reservaRepository.findById(idReserva);
        if(resep.isPresent()){
            return resep.get();
        }
        return null;
    }

    //guardo las reservas
    @Override
    public void guardar(Reserva reserva) {
        reservaRepository.save(reserva);
    }

    public boolean existeReservaParaUsuarioYFecha(Usuario usuario, LocalDate fechaMenu) {
        return reservaRepository.existsByUsuarioAndMenu_FechaMenu(usuario, fechaMenu);
    }

    @Override
    public List<Reserva> obtenerReservasPorUsuario(Usuario usuario) {
        return reservaRepository.findByUsuario(usuario);
    }

}
