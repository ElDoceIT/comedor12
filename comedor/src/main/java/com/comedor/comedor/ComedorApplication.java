package com.comedor.comedor;

import com.comedor.comedor.model.*;
import com.comedor.comedor.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class ComedorApplication {


    public static void main(String[] args) {
        SpringApplication.run(ComedorApplication.class, args);
    }

    public void run(String... args) throws Exception {
    }
}

