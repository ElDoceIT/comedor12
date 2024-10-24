package com.comedor.comedor.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

@Bean
public UserDetailsManager usersCustom(DataSource dataSource) {
    JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
    users.setUsersByUsernameQuery(
            "select dni as username, pass as password,estado as estatus from usuarios where dni = ?"
    );
    users.setAuthoritiesByUsernameQuery(
            "select dni as username, grupo as authority from usuarios where dni = ?"
    );
    return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
// Los recursos estáticos no requieren autenticación
                .requestMatchers("/bootstrap/**","/images/**","/js/**","/css/**").permitAll()
// Las vistas públicas no requieren autenticación
               .requestMatchers( "/signup", "/login").permitAll()

                // Asignar permisos a URLs por ROLES
                .requestMatchers("/comida/**").hasAnyAuthority("Admin", "Comedor")
                .requestMatchers("/usuarios/**").hasAnyAuthority("Admin", "RRHH")
                .requestMatchers("/menu/**").hasAnyAuthority("Admin", "Jefe", "RRHH","Comedor")
                .requestMatchers("/productos/**").hasAnyAuthority("Admin", "Comedor")
                .requestMatchers("/consumos/**").hasAnyAuthority("Admin","RRHH","Comedor")
                .requestMatchers("/reservas/**").hasAnyAuthority("Admin", "Jefe", "Usuario","RRHH","Comedor")
                .requestMatchers("/home/**").hasAnyAuthority("Admin", "Jefe", "Usuario", "RRHH","Comedor")
                .anyRequest().authenticated())
                .formLogin(form -> form
                                .loginPage("/login")  // Especifica la URL de la página de login
                                .usernameParameter("dni")  // Define el parámetro del nombre de usuario (DNI)
                                .passwordParameter("pass")  // Define el parámetro de la contraseña (pass)
                                 .defaultSuccessUrl("/", true)  // Redirigir a /home en caso de éxito
                                .failureUrl("/login?error=true")  // Redirigir en caso de fallo de autenticación
                                .permitAll()  );


// Todas las demás URLs de la Aplicación requieren autenticación

// El formulario de Login no requiere autenticacion
        //http.formLogin(form -> form.permitAll());


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
