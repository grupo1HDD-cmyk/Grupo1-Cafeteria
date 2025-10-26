package com.example.arysu.controllers;

import com.example.arysu.entities.Producto;
import com.example.arysu.entities.Pedido; // ¡Importar Pedido!
import com.example.arysu.enums.EstadoPedido; // ¡Importar EstadoPedido!
import com.example.arysu.services.ProductoService;
import com.example.arysu.services.CategoriaService;
import com.example.arysu.services.PedidoService; // ¡Importar PedidoService!
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PedidoService pedidoService; // <-- ¡NUEVO: Inyectar PedidoService!
    private final String UPLOAD_DIR = "src/main/resources/static/img/productos/";

    // Constructor modificado para inyectar PedidoService
    public AdminController(ProductoService productoService, CategoriaService categoriaService, PedidoService pedidoService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.pedidoService = pedidoService; // <-- ¡Asignar la instancia!
    }

    @GetMapping
    public String adminPanel(Model model) {
        return "admin/dashboard";
    }

    // --- MÉTODOS PARA PRODUCTOS (ya los tenías, solo los incluyo por completitud) ---
    @GetMapping("/productos")
    public String gestionarProductos(Model model) {
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("title", "Gestión de Productos");
        return "admin/productos";
    }

    @GetMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> productoOpt = productoService.buscarPorId(id);
        return productoOpt.map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                         .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/producto/guardar")
    public String guardarProducto(
            @Valid @ModelAttribute Producto producto,
            BindingResult result,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes ra) throws IOException {

        String oldImageUrl = null;
        if (producto.getId() != null) {
            Optional<Producto> existingProductOpt = productoService.buscarPorId(producto.getId());
            if (existingProductOpt.isPresent()) {
                oldImageUrl = existingProductOpt.get().getImagenUrl();
            }
        }

        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Error al guardar el producto. Revisa los campos.");
            ra.addFlashAttribute("validationErrors", result.getAllErrors().stream()
                                                            .map(err -> err.getDefaultMessage())
                                                            .collect(Collectors.joining(", ")));
            return "redirect:/admin/productos";
        }

        if (!imagenFile.isEmpty()) {
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                try {
                    Path oldPath = Paths.get(UPLOAD_DIR).resolve(oldImageUrl.substring(oldImageUrl.lastIndexOf('/') + 1));
                    if (Files.exists(oldPath)) {
                        Files.delete(oldPath);
                        System.out.println("DEBUG: Imagen antigua eliminada: " + oldPath.toString());
                    }
                } catch (IOException e) {
                    System.err.println("ERROR: No se pudo eliminar la imagen antigua: " + e.getMessage());
                }
            }
            
            String fileName = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, imagenFile.getBytes());
            producto.setImagenUrl("/img/productos/" + fileName);
        } else {
            if (producto.getId() != null && oldImageUrl != null) {
                producto.setImagenUrl(oldImageUrl);
            } else {
                if (producto.getId() == null && (producto.getImagenUrl() == null || producto.getImagenUrl().isEmpty())) {
                    ra.addFlashAttribute("error", "La imagen del producto es obligatoria.");
                    return "redirect:/admin/productos";
                }
            }
        }

        productoService.guardarProducto(producto);
        ra.addFlashAttribute("success", "Producto guardado exitosamente!");
        return "redirect:/admin/productos";
    }

    @GetMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Optional<Producto> productoOpt = productoService.buscarPorId(id);
            if (productoOpt.isPresent()) {
                String imageUrl = productoOpt.get().getImagenUrl();
                productoService.eliminarProducto(id);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        Path imagePath = Paths.get(UPLOAD_DIR).resolve(imageUrl.substring(imageUrl.lastIndexOf('/') + 1));
                        if (Files.exists(imagePath)) {
                            Files.delete(imagePath);
                            System.out.println("DEBUG: Archivo de imagen eliminado: " + imagePath.toString());
                        }
                    } catch (IOException e) {
                        System.err.println("ERROR: No se pudo eliminar el archivo de imagen: " + e.getMessage());
                    }
                }
                ra.addFlashAttribute("success", "Producto eliminado exitosamente!");
            } else {
                ra.addFlashAttribute("error", "Producto no encontrado para eliminar.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    // --- ¡NUEVOS MÉTODOS PARA PEDIDOS! ---

    @GetMapping("/pedidos") // <-- ¡ESTE ES EL MÉTODO QUE FALTABA Y CAUSABA EL ERROR!
    public String gestionarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodosPedidos());
        model.addAttribute("title", "Gestión de Pedidos");
        model.addAttribute("estadosPedido", EstadoPedido.values()); // Necesario para el selector de estado en el HTML
        return "admin/pedidos"; // Esto busca src/main/resources/templates/admin/pedidos.html
    }

    @GetMapping("/api/pedidos/{id}") // <-- Endpoint API para obtener detalles de un pedido (para el modal)
    @ResponseBody
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(id);
        return pedidoOpt.map(pedido -> new ResponseEntity<>(pedido, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/pedidos/actualizar-estado/{id}") // <-- Endpoint para actualizar el estado del pedido
    public String actualizarEstadoPedido(@PathVariable Long id,
                                         @RequestParam("estado") EstadoPedido nuevoEstado,
                                         RedirectAttributes ra) {
        try {
            pedidoService.actualizarEstadoPedido(id, nuevoEstado);
            ra.addFlashAttribute("success", "Estado del pedido actualizado exitosamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el estado del pedido: " + e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }
}