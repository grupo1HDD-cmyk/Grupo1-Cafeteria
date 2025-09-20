package com.example.arysu.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.example.arysu.enums.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    private String password;

    @Past(message = "La fecha debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Rol rol; // ADMIN, USER, BARISTA

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pedido> pedidos;
    
    

    // Método para calcular edad (opcional)
    public int getEdad() {
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}