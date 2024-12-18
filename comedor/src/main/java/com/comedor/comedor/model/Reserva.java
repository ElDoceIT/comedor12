package com.comedor.comedor.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name ="reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "f_reserva")
    private LocalDateTime f_reserva;

    @Column(name = "medio")
    private int medio;

    @Column(name = "entregado")
    private LocalDateTime entregado;

    @Column(name = "cant")
    private int cantidad;

    @Column(name = "forzado")
    private Integer forzado;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_menu")
    private Menu menu;





    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDateTime getF_reserva() {
        return f_reserva;
    }

    public void setF_reserva(LocalDateTime f_reserva) {
        this.f_reserva = f_reserva;
    }

    public int getMedio() {
        return medio;
    }

    public void setMedio(int medio) {
        this.medio = medio;
    }

    public LocalDateTime getEntregado() {
        return entregado;
    }

    public void setEntregado(LocalDateTime entregado) {
        this.entregado = entregado;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getForzado() {
        return forzado;
    }

    public void setForzado(int forzado) {
        this.forzado = forzado;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", f_reserva=" + f_reserva +
                ", medio=" + medio +
                ", entregado=" + entregado +
                ", cantidad=" + cantidad +
                ", usuario=" + usuario +
                ", menu=" + menu +
                ", forzado=" + forzado +
                '}';
    }
}
