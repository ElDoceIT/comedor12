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
public class ComedorApplication implements CommandLineRunner {

    @Autowired
   private ProductoRepository productoRepository;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private ReservaRepository reservaRepository;


    public static void main(String[] args) {
        SpringApplication.run(ComedorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //System.out.println("holaaaaaaaaaaaaa");
        //guardar();
        //buscarId();
        //modificar();
        //eliminar();
        //contar();
        //mostrarTodo();
        //existeID();
        //ordenar();
        //paginacion();
        //mostrarTodosComida();
        //mostrarTodosUsuarios();
        //mostrarTodosMenu();
        //mostrarTodosConsumo();
        //mostrarTodosReserva();
    }


    private void guardar() {
        Producto producto = new Producto();
        producto.setDescripcion("Sprite 500ml");
        productoRepository.save(producto);
        System.out.println(producto);
    }
    private void buscarId(){
        Optional<Producto> prod= productoRepository.findById(1);
        if (prod.isPresent()) {
            System.out.println(prod.get());
        }
        else
            System.out.println("No se encontro el producto");
    }

    private void modificar(){
        Optional<Producto> prod =productoRepository.findById(2);
        if (prod.isPresent()) {
            Producto p = prod.get();
            p.setDescripcion("Coca 1500ml");
            productoRepository.save(p);
        }


    }

    private void eliminar(){
        productoRepository.deleteById(4);
    }

    private void contar(){
        long total=productoRepository.count();
        System.out.println("tengo en total "+total);
    }

    private void borrarTodo(){
        productoRepository.deleteAll();
    }

    private void mostrarPorIDs(){
        List<Integer> ids= new LinkedList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        Iterable<Producto> product= productoRepository.findAllById(ids);
        for(Producto pr: product){
            System.out.println(pr);
        }
    }

    private void mostrarTodos(){
        Iterable<Producto> pr=productoRepository.findAll();
        for(Producto prod: pr){
            System.out.println(prod);
        }
    }

    private void existeID(){
        boolean ok =productoRepository.existsById(5);
        if(ok)
            System.out.println("El id existe");
        else System.out.println("El id no existe");
    }

    private void mostrarTodo(){
        productoRepository.findAll().forEach(System.out::println);
    }

    private void ordenar(){
        productoRepository.findAll(Sort.by("descripcion").descending()).forEach(System.out::println);
    }

    private void paginacion(){
        Page<Producto> pagina= productoRepository.findAll(PageRequest.of(1,2));
        System.out.println("tengo en total "+pagina.getTotalElements() + " elementos");
        System.out.println("y tengo "+pagina.getTotalPages() + " paginas ");
        for (Producto producto: pagina.getContent()) {
            System.out.println(producto);
        }
    }

    private void mostrarTodosComida(){
        Iterable<Comida> pr=comidaRepository.findAll();
        for(Comida com : pr){
            System.out.println(com);
        }
    }

    private void mostrarTodosUsuarios(){
        Iterable<Usuario> pr=usuarioRepository.findAll();
        for(Usuario usr : pr){
            System.out.println(usr);
        }
    }

    private void mostrarTodosMenu(){
        Iterable<Menu> pr=menuRepository.findAll();
        for(Menu menu : pr){
            System.out.println(menu);
        }
    }

    private void mostrarTodosConsumo(){
        Iterable<Consumo> pr=consumoRepository.findAll();
        for(Consumo consumo : pr){
            System.out.println(consumo);
        }
    }

    private void mostrarTodosReserva(){
        Iterable<Reserva> pr=reservaRepository.findAll();
        for(Reserva reserva : pr){
            System.out.println(reserva);
        }
    }

}

