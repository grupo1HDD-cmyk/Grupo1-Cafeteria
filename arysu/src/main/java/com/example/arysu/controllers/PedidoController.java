package com.example.arysu.controllers;

import com.example.arysu.entities.Pedido;
import com.example.arysu.entities.DetallePedido;
import com.example.arysu.entities.Usuario;
import com.example.arysu.services.PedidoService;
import com.example.arysu.services.UsuarioService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/pedidos")
@SessionAttributes("carrito")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

// En PedidoController.java
@PostMapping("/procesar")
public String procesarPedido(
        @ModelAttribute("carrito") List<DetallePedido> carrito,
        Authentication authentication,
        // --- ¡NECESITAS OBTENER LA DIRECCIÓN AQUÍ! ---
        // Asumiendo que viene de un campo de formulario llamado 'direccionEntrega'
        @RequestParam("direccionEntrega") String direccionEntrega, // <--- Añade esto
        SessionStatus status) {

    String email = authentication.getName();
    Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

    if (usuarioOpt.isEmpty()) {
        return "redirect:/login?error=usuario-no-encontrado";
    }

    Usuario usuario = usuarioOpt.get();

    // --- ¡CORRECCIÓN CLAVE AQUÍ! Se pasa la dirección ---
    Pedido pedido = pedidoService.crearPedido(usuario, carrito, direccionEntrega); // <--- Pasa direccionEntrega

    status.setComplete();
    return "redirect:/pedidos/comprobante/" + pedido.getId();
}


    @GetMapping("/comprobante/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String comprobante(@PathVariable Long id, Model model) {
    Pedido pedido = pedidoService.buscarPorId(id)
        .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    model.addAttribute("pedido", pedido);
    return "pedidos/comprobante";
}

}
