package com.comedor.comedor.repository;

import com.comedor.comedor.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findByPublicar(int publicar);
    //List<Menu> findAllByFechaMenuAfterOrFechaMenuEquals(LocalDate fecha);
    List<Menu> findMenusByFechaMenuBetween(LocalDate startDate, LocalDate endDate);
}
