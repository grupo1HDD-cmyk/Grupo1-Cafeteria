package com.example.arysu.repositories;

import com.example.arysu.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Buscar por nombre (ignorando mayúsculas/minúsculas)
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
    // Verificar si existe una categoría por nombre (ignorando mayúsculas/minúsculas)
    boolean existsByNombreIgnoreCase(String nombre);
}