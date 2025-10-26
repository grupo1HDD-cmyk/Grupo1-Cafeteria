package com.example.arysu.services;

import com.example.arysu.entities.Categoria;
import com.example.arysu.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Long id) { // <-- ¡Añade este método!
        return categoriaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre);
    }

    @Transactional
    public Categoria guardarCategoria(Categoria categoria) {
        // Validación para evitar categorías duplicadas (¡Esto ya lo tienes, genial!)
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new IllegalArgumentException("La categoría '" + categoria.getNombre() + "' ya existe.");
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Categoria categoria) { // <-- Puedes añadir este método para actualizar
        // Aquí podrías añadir lógica para actualizar una categoría existente
        // Por ejemplo, verificar si el ID existe, y si el nuevo nombre no entra en conflicto con otra categoría.
        if (categoria.getId() == null || !categoriaRepository.existsById(categoria.getId())) {
            throw new IllegalArgumentException("La categoría a actualizar no existe o su ID es nulo.");
        }
        Optional<Categoria> existingCategoryWithName = categoriaRepository.findByNombreIgnoreCase(categoria.getNombre());
        if (existingCategoryWithName.isPresent() && !existingCategoryWithName.get().getId().equals(categoria.getId())) {
            throw new IllegalArgumentException("Ya existe otra categoría con el nombre '" + categoria.getNombre() + "'.");
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminarCategoria(Long id) { // <-- ¡Añade este método!
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("La categoría con ID " + id + " no existe.");
        }
        categoriaRepository.deleteById(id);
    }
}