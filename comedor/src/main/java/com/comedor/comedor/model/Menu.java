package com.comedor.comedor.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_menu;

    @Column(name = "dia")
    private Date dia;

    @OneToOne
    @JoinColumn(name = "comida")
    private Comida comida;

    public Integer getId_menu() {
        return id_menu;
    }

    public void setId_menu(Integer id_menu) {
        this.id_menu = id_menu;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Comida getComida() {
        return comida;
    }

    public void setComida(Comida comida) {
        this.comida = comida;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id_menu=" + id_menu +
                ", dia=" + dia +
                ", comida=" + comida +
                '}';
    }
}
