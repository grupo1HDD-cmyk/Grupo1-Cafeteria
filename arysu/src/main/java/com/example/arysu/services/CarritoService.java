package com.example.arysu.services;

import com.example.arysu.entities.Producto;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;

@Service
@SessionScope
public class CarritoService {

    private List<Producto> items = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        items.add(producto);
    }

    public void eliminarProducto(Long productoId) {
        items.removeIf(p -> p.getId().equals(productoId));
    }

    public List<Producto> getItems() {
        return new ArrayList<>(items);
    }

    public BigDecimal calcularTotal() {
        return items.stream()
            .map(Producto::getPrecio)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vaciarCarrito() {
        items.clear();
    }
}