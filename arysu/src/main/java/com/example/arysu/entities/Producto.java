package com.example.arysu.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importar esto si no lo tienes

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "productos")
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio mínimo es 0.01")
    private BigDecimal precio;

    @Size(max = 255, message = "Máximo 255 caracteres")
    private String descripcion;

    /*@NotBlank(message = "La imagen es obligatoria")*/
    private String imagenUrl;

    // --- ¡CORRECCIÓN CLAVE AQUÍ! Cambiado a FetchType.EAGER ---
    @ManyToOne(fetch = FetchType.EAGER) // <--- ¡CAMBIA ESTO A EAGER!
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // --- ¡Asegúrate de que @JsonIgnore esté aquí para evitar bucles con DetallePedido! ---
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // <--- ¡IMPORTANTE! Para evitar bucles de serialización JSON.
    private List<DetallePedido> detalles;

    // Tu constructor manual (puedes mantenerlo si lo usas explícitamente en algún lugar)
    public Producto(String nombre, BigDecimal precio, String imagen, Categoria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagenUrl = imagen;
        this.categoria = categoria;
    }
}