package com.comedor.comedor.dto;

import com.comedor.comedor.model.Menu;

import java.time.LocalDate;


public class ReservaForm {
    private LocalDate f_reserva;
    private Menu menu;
    private int medio;

    public LocalDate getF_reserva() {
        return f_reserva;
    }

    public void setF_reserva(LocalDate f_reserva) {
        this.f_reserva = f_reserva;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getMedio() {
        return medio;
    }

    public void setMedio(int medio) {
        this.medio = medio;
    }
}
