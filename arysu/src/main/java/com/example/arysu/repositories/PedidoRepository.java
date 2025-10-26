package com.example.arysu.repositories;

import com.example.arysu.entities.Pedido;
import com.example.arysu.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos por usuario
    List<Pedido> findByUsuarioId(Long usuarioId);

    // Buscar pedidos por estado
    List<Pedido> findByEstado(EstadoPedido estado);

    // Consulta personalizada: Pedidos entre fechas (para reportes)
    @Query("SELECT p FROM Pedido p WHERE p.fecha BETWEEN ?1 AND ?2")
    List<Pedido> findPedidosBetweenDates(LocalDateTime inicio, LocalDateTime fin);

    // Actualizar estado de un pedido (sin cargar toda la entidad)
    @Modifying
    @Query("UPDATE Pedido p SET p.estado = ?2 WHERE p.id = ?1")
    void updateEstado(Long pedidoId, EstadoPedido estado);
}