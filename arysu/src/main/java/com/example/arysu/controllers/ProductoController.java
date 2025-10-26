package com.example.arysu.controllers;

import com.example.arysu.entities.Categoria; // Importa la entidad Categoria
import com.example.arysu.services.CategoriaService; // Importa CategoriaService
import com.example.arysu.services.ProductoService;
import jakarta.servlet.http.HttpServletRequest; // Importa HttpServletRequest
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Importa RequestParam

import java.util.Collections; // Para Collections.singletonList
import java.util.List; // Importa List

@Controller
@RequestMapping("/productos") // Base path for product-related operations
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService; // Inyecta CategoriaService

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService; // Inicializa CategoriaService
    }

    // Método para listar TODOS los productos (generalmente para un panel de administración, no el menú público)
    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.listarProductos());
        return "productos/lista"; // Asume que tienes una vista 'lista.html' en 'productos/'
    }

    // Método para mostrar el detalle o formulario de un producto específico
    @GetMapping("/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto inválido: " + id)));
        return "productos/formulario"; // Asume que 'formulario.html' es para ver/editar un producto
    }

    @GetMapping("/menu") // La URL para ver el menú será /productos/menu
    public String mostrarMenu(@RequestParam(name = "categoriaId", required = false) Long categoriaId,
                              Model model,
                              HttpServletRequest request) {

        List<Categoria> categorias;

        if (categoriaId == null) {
            // Si no se especifica una categoría (o se selecciona "Todo"), carga todas las categorías.
            // Gracias a FetchType.EAGER en Categoria, sus productos ya vienen cargados.
            categorias = categoriaService.listarCategorias();
        } else {
            // Si se selecciona una categoría específica, busca esa categoría.
            Categoria categoriaSeleccionada = categoriaService.buscarPorId(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("ID de categoría inválido: " + categoriaId));
            // Creamos una lista que contenga solo la categoría seleccionada.
            // Tu menu.html espera una lista de categorías para iterar.
            categorias = Collections.singletonList(categoriaSeleccionada);
        }

        model.addAttribute("categorias", categorias); // Pasar la lista (filtrada o completa) al modelo
        model.addAttribute("content", "pages/menu"); // Ruta a tu archivo menu.html
        model.addAttribute("title", "Menú"); // Título para la página
        model.addAttribute("requestURI", request.getRequestURI()); // Para el fragmento de layout

        return "fragments/layout"; // Retorna el layout general que envuelve menu.html
    }
}