package com.example.arysu.repositories;

import com.example.arysu.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por email (para login)
    Optional<Usuario> findByEmail(String email);

    // Verificar si un email existe (para validar registros)
    boolean existsByEmail(String email);

    // Consulta personalizada: Usuarios con más pedidos (para estadísticas)
    @Query("SELECT u FROM Usuario u ORDER BY SIZE(u.pedidos) DESC")
    List<Usuario> findTopUsersByPedidos();
}