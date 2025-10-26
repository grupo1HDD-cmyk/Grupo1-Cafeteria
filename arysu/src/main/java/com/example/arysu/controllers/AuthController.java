package com.example.arysu.controllers;

import com.example.arysu.entities.Usuario;
import com.example.arysu.enums.Rol;
import com.example.arysu.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest; // Importa HttpServletRequest

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Formulario de login
    @GetMapping("/login")
    public String loginForm(Model model, HttpServletRequest request) { // ¡Añade HttpServletRequest request aquí!
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("title", "Iniciar Sesión");
        model.addAttribute("content", "auth/login");
        model.addAttribute("requestURI", request.getRequestURI()); // <--- ¡Añade esta línea!
        return "fragments/layout";
    }

    // Formulario de registro
    @GetMapping("/registro")
    public String registroForm(Model model, HttpServletRequest request) { // ¡Añade HttpServletRequest request aquí!
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("title", "Registrarse");
        model.addAttribute("content", "auth/registro");
        model.addAttribute("requestURI", request.getRequestURI()); // <--- ¡Añade esta línea!
        return "fragments/layout";
    }

    // Procesar registro (este método no necesita HttpServletRequest porque redirige)
    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute Usuario usuario,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Registrarse");
            model.addAttribute("content", "auth/registro");
            // Puedes añadir el requestURI aquí también, aunque generalmente un POST con errores
            // vuelve a la misma página y el requestURI no cambia mucho.
            // Si el layout lo necesita, agrégalo:
            // model.addAttribute("requestURI", request.getRequestURI()); // Tendrías que inyectar request aquí también
            return "fragments/layout";
        }

        if (usuarioService.existsByEmail(usuario.getEmail())) {
            model.addAttribute("error", "El correo ya está registrado");
            model.addAttribute("title", "Registrarse");
            model.addAttribute("content", "auth/registro");
            // Si el layout lo necesita, agrégalo:
            // model.addAttribute("requestURI", request.getRequestURI()); // Tendrías que inyectar request aquí también
            return "fragments/layout";
        }
        usuario.setRol(Rol.USER);
        usuarioService.registrarUsuario(usuario);
        return "redirect:/auth/login?registroExitoso";
    }
}