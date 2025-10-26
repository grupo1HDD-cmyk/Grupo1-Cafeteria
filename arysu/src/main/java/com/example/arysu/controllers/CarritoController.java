package com.example.arysu.controllers;

import com.example.arysu.entities.CartItem;
import com.example.arysu.entities.DetallePedido;
import com.example.arysu.entities.Producto;
import com.example.arysu.entities.Usuario;
import com.example.arysu.services.PedidoService;
import com.example.arysu.services.ProductoService;
import com.example.arysu.services.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity; // Importa ResponseEntity

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private static final String CART_SESSION_KEY = "userBackendCart";

    private final PedidoService pedidoService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public CarritoController(PedidoService pedidoService, ProductoService productoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/ver")
    @SuppressWarnings("unchecked")
    public String showCartPage(Model model, HttpSession session, HttpServletRequest request) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
        }
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", calculateTotalPrice(cart));
        model.addAttribute("title", "Tu Carrito");
        model.addAttribute("content", "carrito/ver");
        model.addAttribute("requestURI", request.getRequestURI());
        return "fragments/layout";
    }

    @PostMapping("/confirmar-pedido")
    @ResponseBody 
    public ResponseEntity<?> receiveCartForCheckout(@RequestBody List<CartItem> cartItems, HttpSession session, RedirectAttributes ra) {
        List<CartItem> validatedCart = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            Optional<Producto> productoOpt = productoService.buscarPorId(item.getId());
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                if (producto.getPrecio().compareTo(item.getPrecio()) != 0) {
                    item.setPrecio(producto.getPrecio()); 
                }
                validatedCart.add(item);
            } else {
                return ResponseEntity.badRequest().body("Uno o más productos en tu carrito ya no están disponibles.");
            }
        }
        
        session.setAttribute(CART_SESSION_KEY, validatedCart);
        return ResponseEntity.ok().build(); 
    }

    @GetMapping("/checkout")
    @SuppressWarnings("unchecked")
    public String showCheckoutPage(Model model, HttpSession session, HttpServletRequest request, RedirectAttributes ra) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        
        // --- ¡CORRECCIÓN CLAVE AQUÍ! ---
        // Obtener el flashAttribute para el modal.
        // Si existe un mensaje de éxito, significa que un pedido acaba de ser realizado,
        // por lo tanto, no redirigimos aunque el carrito esté vacío.
        String successMessageForModal = (String) model.asMap().get("successMessageForModal");

        if ((cart == null || cart.isEmpty()) && successMessageForModal == null) { 
            ra.addFlashAttribute("error", "Tu carrito está vacío o la sesión expiró. Por favor, añade productos de nuevo.");
            return "redirect:/carrito/ver";
        }

        model.addAttribute("itemsCarrito", cart); 
        model.addAttribute("totalCarrito", calculateTotalPrice(cart));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String userEmail = authentication.getName(); 
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(userEmail);
            usuarioOpt.ifPresent(usuario -> model.addAttribute("currentUser", usuario));
        }

        model.addAttribute("title", "Finalizar Compra");
        model.addAttribute("content", "carrito/checkout");
        model.addAttribute("requestURI", request.getRequestURI());
        return "fragments/layout";
    }

    @PostMapping("/procesar-pedido")
    @SuppressWarnings("unchecked")
    public String processOrder(@RequestParam String address,
                               @RequestParam String paymentMethod,
                               HttpSession session,
                               RedirectAttributes ra) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("\n--- DEBUG de /procesar-pedido ---");
        System.out.println("DEBUG: Autenticación: " + authentication);
        if (authentication != null) {
            System.out.println("DEBUG: Principal: " + authentication.getPrincipal());
            System.out.println("DEBUG: Nombre de usuario (de Authentication.getName()): " + authentication.getName());
            System.out.println("DEBUG: Tipo de autenticación: " + authentication.getClass().getSimpleName());
            System.out.println("DEBUG: Autoridades (Roles): " + authentication.getAuthorities());
            System.out.println("DEBUG: ¿Está autenticado?: " + authentication.isAuthenticated());
            System.out.println("DEBUG: ¿Es anónimo?: " + (authentication instanceof AnonymousAuthenticationToken));
        } else {
            System.out.println("DEBUG: Objeto Authentication es null.");
        }
        System.out.println("--- FIN DEBUG ---");

        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        System.out.println("DEBUG: Carrito cargado. Número de ítems: " + (cart != null ? cart.size() : "null"));

        if (cart == null || cart.isEmpty()) {
            System.out.println("DEBUG: Redirigiendo a /carrito/ver porque el carrito está vacío.");
            ra.addFlashAttribute("error", "El carrito está vacío. Por favor, añade productos para procesar el pedido.");
            return "redirect:/carrito/ver";
        }

        if (authentication == null || !authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)) {
            System.out.println("DEBUG: Redirigiendo a login porque el usuario no está autenticado o es anónimo.");
            ra.addFlashAttribute("error", "Debes iniciar sesión para completar tu pedido.");
            return "redirect:/auth/login";
        }

        String userEmail = authentication.getName();
        System.out.println("DEBUG: Buscando usuario con email: " + userEmail);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(userEmail);

        if (usuarioOpt.isEmpty()) {
            System.out.println("DEBUG: Usuario NO encontrado para email: " + userEmail + ". Redirigiendo a login.");
            ra.addFlashAttribute("error", "No se pudo encontrar la información del usuario logueado.");
            return "redirect:/auth/login";
        }
        System.out.println("DEBUG: Usuario encontrado: " + usuarioOpt.get().getNombres() + " (" + userEmail + ")");
        Usuario usuario = usuarioOpt.get();

        List<DetallePedido> detalles = new ArrayList<>();

        for (CartItem item : cart) {
            Optional<Producto> productoOpt = productoService.buscarPorId(item.getId());
            if (productoOpt.isEmpty()) {
                System.out.println("DEBUG: Producto NO encontrado para ID: " + item.getId() + ". Redirigiendo a /carrito/ver.");
                ra.addFlashAttribute("error", "Uno o más productos en tu carrito ya no están disponibles. Por favor, revisa tu carrito.");
                return "redirect:/carrito/ver";
            }
            System.out.println("DEBUG: Producto encontrado: " + productoOpt.get().getNombre() + " (ID: " + item.getId() + ")");

            Producto producto = productoOpt.get();

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalles.add(detalle);
        }

        try {
            System.out.println("DEBUG: ANTES de llamar a crearPedido.");
            // --- ¡CORRECCIÓN CLAVE AQUÍ! Se pasa el tercer argumento 'address' ---
            pedidoService.crearPedido(usuario, detalles, address); 
            System.out.println("DEBUG: DESPUÉS de llamar a crearPedido. Pedido creado con éxito.");

            session.removeAttribute(CART_SESSION_KEY);
            ra.addFlashAttribute("successMessageForModal", "¡Tu pedido ha sido realizado con éxito! Dirección: " + address);
            System.out.println("DEBUG: Redirigiendo a /carrito/checkout");
            return "redirect:/carrito/checkout";
        } catch (Exception e) {
            System.err.println("ERROR: Excepción al procesar pedido: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("errorMessageForModal", "Hubo un error al procesar tu pedido. Por favor, inténtalo de nuevo.");
            return "redirect:/carrito/checkout";
        }
    }

    private BigDecimal calculateTotalPrice(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.stream()
                .map(CartItem::getPrecioTotal) 
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}