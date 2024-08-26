package com.comedor.comedor.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "comidas")
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id_comida;

    @Column(name = "entrada")
    private String entrada;

    @Column(name ="principal")
    private String comida;

    @Column(name ="postre")
    private String postre;

    @Column(name ="tipo")
    private int tipo_comida;

    public Integer getId_comida() {
        return id_comida;
    }

    public void setId_comida(Integer id_comida) {
        this.id_comida = id_comida;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getComida() {
        return comida;
    }

    public void setComida(String comida) {
        this.comida = comida;
    }

    public String getPostre() {
        return postre;
    }

    public void setPostre(String postre) {
        this.postre = postre;
    }

    public int getTipo_comida() {
        return tipo_comida;
    }

    public void setTipo_comida(int tipo_comida) {
        this.tipo_comida = tipo_comida;
    }

    @Override
    public String toString() {
        return "Comida{" +
                "id_comida=" + id_comida +
                ", entrada='" + entrada + '\'' +
                ", comida='" + comida + '\'' +
                ", postre='" + postre + '\'' +
                ", tipo_comida=" + tipo_comida +
                '}';
    }
}
