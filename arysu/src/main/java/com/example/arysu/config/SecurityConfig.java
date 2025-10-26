package com.example.arysu.config;

import org.springframework.context.annotation.Bean;
import com.example.arysu.auth.CustomUserDetailService; // Asegúrate de que esta importación sea correcta
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // ¡IMPORTAR ESTA ANOTACIÓN!
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler; // ¡IMPORTAR ESTA CLASE!
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler; // ¡IMPORTAR ESTA CLASE!
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Necesitas esta importación para POST /confirmar-pedido


@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita la seguridad a nivel de método (para @PreAuthorize)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Mantener deshabilitado si no usas el token CSRF en tus formularios
            .authorizeHttpRequests(auth -> auth
                // 1. Permitir acceso a recursos estáticos y páginas de autenticación/registro/error
                .requestMatchers(
                    "/css/**", "/js/**", "/img/**", "/error/**",
                    "/auth/**", "/usuario/registro"
                ).permitAll()

                // 2. Rutas públicas (cualquiera puede acceder)
                .requestMatchers(
                    "/", "/nosotros", "/contacto", "/menu",
                    "/carrito", "/carrito/ver"
                ).permitAll()
                
                // 3. Permitir el POST para confirmar el carrito sin requerir login aún.
                .requestMatchers(new AntPathRequestMatcher("/carrito/confirmar-pedido", "POST")).permitAll()
                
                // 4. Rutas de ADMIN (protegidas por rol ADMIN o BARISTA)
                // Si tienes rol BARISTA que también puede acceder al admin dashboard, inclúyelo aquí.
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "BARISTA") // Usar hasAnyRole para múltiples roles
                
                // 5. Rutas de CHECKOUT/PEDIDOS/PAGO que requieren que el usuario esté logueado con rol USER
                .requestMatchers(
                    "/carrito/checkout",             // Página de checkout (GET)
                    "/carrito/procesar-pedido",      // POST para guardar el pedido
                    "/pedidos/**",                   // Páginas de gestión de pedidos (si el usuario normal las ve)
                    "/pago/**"                       // Páginas de pago (si tienes más)
                ).hasRole("USER") 
                
                // 6. Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login") // Página de login personalizada
                // --- ¡CORRECCIÓN CLAVE AQUÍ! ---
                // Reemplazamos defaultSuccessUrl por un successHandler personalizado
                .successHandler(customAuthenticationSuccessHandler()) 
                .failureUrl("/auth/login?error=true")
                .permitAll() // Permite acceso a la página de login sin autenticación
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403") // Página para acceso denegado (rol incorrecto)
            );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // --- ¡NUEVO BEAN PARA EL MANEJADOR DE ÉXITO DE AUTENTICACIÓN! ---
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // Verifica si el usuario autenticado tiene el rol ADMIN o BARISTA
            if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_BARISTA"))) {
                
                // Si es ADMIN o BARISTA, redirigir al dashboard de admin
                // Usamos SavedRequestAwareAuthenticationSuccessHandler para manejar la redirección
                SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
                handler.setDefaultTargetUrl("/admin"); // URL por defecto para ADMIN/BARISTA
                handler.setAlwaysUseDefaultTargetUrl(true); // Siempre redirigir a esta URL para ADMIN/BARISTA
                handler.onAuthenticationSuccess(request, response, authentication);
            } else {
                // Para otros roles (clientes), redirigir a la URL solicitada originalmente o a la raíz
                SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
                handler.setDefaultTargetUrl("/"); // URL por defecto para usuarios normales (ej. inicio)
                handler.setAlwaysUseDefaultTargetUrl(false); // No siempre, si había una URL previa guardada
                handler.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
}