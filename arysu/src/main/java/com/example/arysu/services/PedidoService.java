package com.example.arysu.services;

import com.example.arysu.entities.*;
import com.example.arysu.repositories.PedidoRepository;
import com.example.arysu.repositories.DetallePedidoRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.example.arysu.enums.EstadoPedido;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Importar Objects para chequeo de nulos

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional
    public Pedido crearPedido(Usuario usuario, List<DetallePedido> detalles, String direccionEntrega) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto.");
        }
        if (direccionEntrega == null || direccionEntrega.isBlank()) {
            throw new IllegalArgumentException("La dirección de entrega no puede estar vacía.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setDireccionEntrega(direccionEntrega);

        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido detalle : detalles) {
            if (detalle.getProducto() != null && detalle.getProducto().getPrecio() != null) {
                detalle.setPedido(pedido); // Asociar el detalle al pedido
                total = total.add(detalle.getProducto().getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            } else {
                throw new IllegalStateException("Producto o precio nulo en detalle de pedido para producto ID: " + (detalle.getProducto() != null ? detalle.getProducto().getId() : "N/A"));
            }
        }
        pedido.setTotal(total);
        pedido.setDetalles(detalles); 

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return pedidoGuardado;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }
    
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // --- ¡NUEVOS MÉTODOS A AGREGAR! ---

    /**
     * Lista todos los pedidos registrados en el sistema.
     * @return Una lista de todos los pedidos.
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.findAll(); // Asume que PedidoRepository extiende JpaRepository
    }

    /**
     * Actualiza el estado de un pedido específico.
     * @param pedidoId El ID del pedido a actualizar.
     * @param nuevoEstado El nuevo estado que se le asignará al pedido.
     * @return El pedido actualizado.
     * @throws IllegalArgumentException si el pedido no es encontrado.
     */
    @Transactional
    public Pedido actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                                        .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
}