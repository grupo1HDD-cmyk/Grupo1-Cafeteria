package com.example.arysu.entities;

import com.fasterxml.jackson.annotation.JsonIgnore; // ¡IMPORTAR ESTO!
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "categorias")
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "Máximo 50 caracteres")
    @Column(unique = true)
    private String nombre;

    // --- ¡AÑADE @JsonIgnore AQUÍ! ---
    // Esto le dice a Jackson que ignore este campo al serializar una Categoria a JSON
    @OneToMany(mappedBy = "categoria", fetch = FetchType.EAGER)
    @JsonIgnore // <-- ¡Esta es la modificación clave!
    private List<Producto> productos;

    public Categoria(String nombre) {
        this.nombre = nombre;
    }
}