package com.example.arysu.repositories;

import com.example.arysu.entities.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Buscar registros por usuario y fecha
    List<Auditoria> findByUsuarioIdAndFechaBetween(
        Long usuarioId, 
        LocalDateTime inicio, 
        LocalDateTime fin
    );
}