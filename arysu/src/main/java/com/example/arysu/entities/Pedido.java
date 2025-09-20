package com.example.arysu.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.example.arysu.enums.EstadoPedido;
import java.math.BigDecimal;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true) // Añadido orphanRemoval para DetallePedido
    private List<DetallePedido> detalles;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado; // PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO

    @DecimalMin(value = "0.00", message = "El total no puede ser negativo")
    private BigDecimal total;

    // --- ¡NUEVO CAMPO PARA LA DIRECCIÓN DEL PEDIDO! ---
    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 255, message = "Máximo 255 caracteres para la dirección")
    private String direccionEntrega; // O solo 'direccion' si lo prefieres, pero 'direccionEntrega' es más claro.
}