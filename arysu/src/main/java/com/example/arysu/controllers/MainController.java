
package com.example.arysu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam; // Importar RequestParam
import jakarta.servlet.http.HttpServletRequest; // Mantener para requestURI si lo usas

import com.example.arysu.services.CategoriaService;

@Controller
public class MainController {

    private final CategoriaService categoriaService;

    public MainController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) { // Mantener HttpServletRequest si lo usas para requestURI
        model.addAttribute("content", "pages/index");
        model.addAttribute("title", "Inicio");
        model.addAttribute("requestURI", request.getRequestURI());
        return "fragments/layout";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model, HttpServletRequest request) {
        model.addAttribute("content", "pages/nosotros");
        model.addAttribute("title", "Nosotros");
        model.addAttribute("requestURI", request.getRequestURI());
        return "fragments/layout";
    }

    @GetMapping("/menu")
    // Aquí es donde cambiaremos la forma de obtener el parametro
    public String menu(Model model, HttpServletRequest request,
                       @RequestParam(value = "categoriaId", required = false) String categoriaId) {
        
        model.addAttribute("categorias", categoriaService.listarCategorias());

        model.addAttribute("content", "pages/menu");
        model.addAttribute("title", "Menú");
        model.addAttribute("requestURI", request.getRequestURI()); // Puedes mantener esto si lo usas en el layout o header

        // Pasa el valor del parametro directamente al modelo
        // Ahora Thymeleaf recibirá "categoriaIdActual" en lugar de intentar acceder a #request
        model.addAttribute("categoriaIdActual", categoriaId); 
        
        return "fragments/layout";
    }

    @GetMapping("/contacto")
    public String contacto(Model model, HttpServletRequest request) {
        model.addAttribute("content", "pages/contacto");
        model.addAttribute("title", "Contáctanos");
        model.addAttribute("requestURI", request.getRequestURI());
        return "fragments/layout";
    }
}