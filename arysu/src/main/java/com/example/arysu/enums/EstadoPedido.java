package com.example.arysu.enums;

public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En Preparación"), // Cambiado de EN_PROCESO a EN_PREPARACION (o puedes mantener EN_PROCESO si lo prefieres)
    ENVIADO("Enviado"),              // ¡Nuevo estado!
    COMPLETADO("Completado"),
    CANCELADO("Cancelado");

    private final String displayName; // Campo para el nombre amigable

    // Constructor que asigna el nombre amigable a cada estado
    EstadoPedido(String displayName) {
        this.displayName = displayName;
    }

    // Getter para acceder al nombre amigable
    public String getDisplayName() {
        return displayName;
    }
}