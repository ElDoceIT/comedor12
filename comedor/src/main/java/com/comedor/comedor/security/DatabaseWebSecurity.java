package com.comedor.comedor.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
         //       .requestMatchers("/", "/signup", "/search", "/vacantes/view/**").permitAll()

                // Asignar permisos a URLs por ROLES
                .requestMatchers("/comida/**").hasAnyAuthority("Usuario","Admin", "Jefe")
                .requestMatchers("/usuarios/**").hasAnyAuthority("Admin", "Jefe")
                .requestMatchers("/menu/**").hasAnyAuthority("Admin", "Jefe")
                .requestMatchers("/productos/**").hasAnyAuthority("Admin", "Jefe")
                .requestMatchers("/consumos/**").hasAnyAuthority("Admin", "Jefe")
// Todas las demás URLs de la Aplicación requieren autenticación
                .anyRequest().authenticated());
// El formulario de Login no requiere autenticacion
        http.formLogin(form -> form.permitAll());

        return http.build();
    }

}