package com.comedor.comedor.security;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.session.SessionManagementFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

    @Bean
    public UserDetailsManager usersCustom(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery(
                "select dni as username, pass as password, estado as estatus from usuarios where dni = ?"
        );
        users.setAuthoritiesByUsernameQuery(
                "select dni as username, grupo as authority from usuarios where dni = ?"
        );
        return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/bootstrap/**", "/images/**", "/js/**", "/css/**").permitAll()
                        .requestMatchers("/signup", "/login").permitAll()
                        .requestMatchers("/comida/**").hasAnyAuthority("Admin", "Comedor")
                        .requestMatchers("/usuarios/**").hasAnyAuthority("Admin", "RRHH")
                        .requestMatchers("/menu/**").hasAnyAuthority("Admin", "Jefe", "RRHH","Comedor")
                        .requestMatchers("/productos/**").hasAnyAuthority("Admin", "Comedor")
                        .requestMatchers("/consumos/**").hasAnyAuthority("Admin","RRHH","Comedor")
                        .requestMatchers("/reservas/**").hasAnyAuthority("Admin", "Jefe", "Usuario","RRHH","Comedor")
                        .requestMatchers("/home/**").hasAnyAuthority("Admin", "Jefe", "Usuario", "RRHH","Comedor")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("dni")
                        .passwordParameter("pass")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                        .successHandler(customAuthenticationSuccessHandler()) // Añade el handler para gestionar el tiempo de sesión
                )
                .sessionManagement(session -> session
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                                .expiredUrl("/login?expired=true")
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // Verifica si el usuario tiene el rol "comedor" y establece el tiempo de espera
            boolean isComedor = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("Comedor"));

            // 60 minutos (3600 segundos) para "comedor", 30 minutos (1800 segundos) para otros
            session.setMaxInactiveInterval(isComedor ? 3600 : 300);

            response.sendRedirect(request.getContextPath() + "/"); // Redirige a la página de inicio tras el login
        };
    }
}
