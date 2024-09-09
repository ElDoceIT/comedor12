package com.comedor.comedor.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "consumos")
public class Consumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consumo")
    private Integer idConsumo;

    @Column(name ="cant")
    private int cantidad;


    @Column(name ="fecha")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;

    @OneToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public Integer getIdConsumo() {
        return idConsumo;
    }

    public void setIdConsumo(Integer idConsumo) {
        this.idConsumo = idConsumo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Consumo{" +
                "idConsumo=" + idConsumo +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", producto=" + producto +
                ", usuario=" + usuario +
                '}';
    }
}
