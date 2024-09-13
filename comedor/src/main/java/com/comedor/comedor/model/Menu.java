package com.comedor.comedor.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_menu;

    @Column(name = "dia")
    private LocalDate fechaMenu;

    @OneToOne
    @JoinColumn(name = "comida")
    private Comida comida;

    @Column(name = "publica")
    private Integer publicar;

    public Integer getId_menu() {
        return id_menu;
    }

    public void setId_menu(Integer id_menu) {
        this.id_menu = id_menu;
    }

    public LocalDate getFechaMenu() {
        return fechaMenu;
    }

    public void setFechaMenu(LocalDate dia) {
        this.fechaMenu = dia;
    }

    public Comida getComida() {
        return comida;
    }

    public void setComida(Comida comida) {
        this.comida = comida;
    }

    public Integer getPublicar() {
        return publicar;
    }

    public void setPublicar(Integer publicar) {
        this.publicar = publicar;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id_menu=" + id_menu +
                ", dia=" + fechaMenu +
                ", comida=" + comida +
                ", publicar=" + publicar +
                '}';
    }

}
