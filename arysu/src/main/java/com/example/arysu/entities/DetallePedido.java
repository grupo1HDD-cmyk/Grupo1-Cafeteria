package com.example.arysu.entities;

import com.fasterxml.jackson.annotation.JsonIgnore; // ¡Importar esta anotación!
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Data
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Puedes usar LAZY aquí para mejor rendimiento si no siempre necesitas el pedido completo
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore // ¡IMPORTANTE! Evita bucle infinito al serializar Pedido -> Detalles -> Pedido
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para que el Producto se cargue automáticamente con el detalle
    @JoinColumn(name = "producto_id", nullable = false)
    // Para el JSON del modal de detalles del pedido, queremos el producto, pero NO el DetallePedido
    // de vuelta, por lo que aquí no necesitas @JsonIgnore en `producto` si quieres serializar el producto.
    // Sin embargo, en Producto.java, la lista de detalles_pedido SÍ debe tener @JsonIgnore
    // Para evitar que el producto serialice detalles, y el detalle serialice el producto, etc.
    // Si tu modal muestra detalles del producto (nombre, precio, etc.), entonces NO pongas @JsonIgnore aquí.
    // Pero si solo necesitas el ID, podrías considerarlo.

    // A menudo, para los detalles de pedido en un modal, quieres la información del producto.
    // Por lo tanto, mantendremos este campo sin @JsonIgnore aquí.
    // La clave es que la relación inversa (en Producto hacia DetallePedido) tenga @JsonIgnore.
    private Producto producto;

    @Min(value = 1, message = "La cantidad mínima es 1")
    private int cantidad;

    @DecimalMin(value = "0.01", message = "El precio no puede ser cero")
    private BigDecimal precioUnitario;
}