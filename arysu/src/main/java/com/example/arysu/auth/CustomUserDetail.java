package com.example.arysu.auth;

import com.example.arysu.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetail implements UserDetails {

    private final Usuario usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aseguramos que el rol siempre tenga el prefijo "ROLE_"
        // para que coincida con lo que 'hasRole("USER")' espera en SecurityConfig.
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        // --- ¡CORRECCIÓN CLAVE AQUÍ! ---
        // getUsername() DEBE devolver el identificador único usado para el login, que es el email.
        // Spring Security usa este método para identificar al usuario en el contexto de seguridad.
        return usuario.getEmail(); 
    }

    // --- NUEVO MÉTODO para obtener el nombre a mostrar en la UI ---
    // Usa este método en tus plantillas HTML (ej. en el header) para mostrar el nombre.
    public String getDisplayName() {
        if (usuario != null && usuario.getNombres() != null) {
            return usuario.getNombres();
        }
        // Fallback si el nombre es nulo
        return usuario.getEmail(); 
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return true; 
    }
}