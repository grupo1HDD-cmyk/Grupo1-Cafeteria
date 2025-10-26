package com.example.arysu.repositories;

import com.example.arysu.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos por categoría
    List<Producto> findByCategoriaId(Long categoriaId);

    // Buscar productos con precio menor o igual a X (para filtros)
    List<Producto> findByPrecioLessThanEqual(BigDecimal precio);

    // Consulta personalizada: Productos más vendidos
    @Query("SELECT p FROM Producto p JOIN p.detalles d GROUP BY p ORDER BY SUM(d.cantidad) DESC")
    List<Producto> findTopSellingProducts();
}