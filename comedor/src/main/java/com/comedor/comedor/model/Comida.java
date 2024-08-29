package com.comedor.comedor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comidas")
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "entrada")
    private String entrada;

    @Column(name ="principal")
    private String principal;

    @Column(name ="postre")
    private String postre;

    @Column(name ="tipo")
    private int tipo_comida;

    public Integer getId_comida() {
        return id;
    }

    public void setId_comida(Integer id) {
        this.id = id;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String comida) {
        this.principal = comida;
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
                "id_comida=" + id+
                ", entrada='" + entrada + '\'' +
                ", comida='" + principal + '\'' +
                ", postre='" + postre + '\'' +
                ", tipo_comida=" + tipo_comida +
                '}';
    }
}
