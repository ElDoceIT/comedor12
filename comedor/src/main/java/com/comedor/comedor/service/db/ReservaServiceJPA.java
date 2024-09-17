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

    @Override
    public Reserva guardarReserva(ReservaForm reservaForm, Usuario usuario) {
        Reserva reserva = new Reserva();
        reserva.setF_reserva(reservaForm.getF_reserva());
        reserva.setMenu(reservaForm.getMenu());
        reserva.setUsuario(usuario);

        // Medio de comida es un número
        reserva.setMedio(reservaForm.getMedio());  // Esto será un int (1, 2 o 3)

        return reservaRepository.save(reserva);
    }
}
