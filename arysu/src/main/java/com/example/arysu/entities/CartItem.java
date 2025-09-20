package com.example.arysu.entities; // <-- ¡Verifica que este sea el paquete correcto en tu proyecto!

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal; // Siempre usa BigDecimal para moneda

@Data // Anotación de Lombok: Genera automáticamente getters, setters, toString, equals, hashCode
@NoArgsConstructor // Anotación de Lombok: Genera un constructor sin argumentos
@AllArgsConstructor // Anotación de Lombok: Genera un constructor con todos los argumentos
public class CartItem {

    private Long id; // ID del producto (debe coincidir con el id de tu entidad Producto)
    private String nombre; // Nombre del producto
    private BigDecimal precio; // Precio unitario del producto (usar BigDecimal para precisión)
    private int cantidad; // Cantidad de este producto en el carrito

    // *** NUEVO: Método para calcular el precio total de este item ***
    // Thymeleaf lo llamará automáticamente como 'item.precioTotal'
    public BigDecimal getPrecioTotal() {
        // Asegúrate de que ni precio ni cantidad sean nulos antes de la multiplicación
        if (precio == null || cantidad < 0) { // O maneja de forma diferente si cantidad puede ser 0
            return BigDecimal.ZERO;
        }
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}