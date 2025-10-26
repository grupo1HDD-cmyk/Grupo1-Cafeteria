package com.example.arysu;

import java.math.BigDecimal;
import com.example.arysu.enums.Rol;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.arysu.entities.Categoria;
import com.example.arysu.entities.Producto;
import com.example.arysu.entities.Usuario;
import com.example.arysu.repositories.CategoriaRepository;
import com.example.arysu.repositories.ProductoRepository;
import com.example.arysu.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;

@SpringBootApplication
public class ArysuApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArysuApplication.class, args);
	}

@Bean
    CommandLineRunner initData(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            PasswordEncoder  passwordEncoder
    ) {
        return args -> {
            // Usuarios
            if (usuarioRepository.findByEmail("admin@arysu.pe").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombres("Admin");
                admin.setApellidos("Principal");
                admin.setEmail("admin@arysu.pe");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol(Rol.ADMIN);
                usuarioRepository.save(admin);
            }

            if (usuarioRepository.findByEmail("barista@arysu.pe").isEmpty()) {
                Usuario barista = new Usuario();
                barista.setNombres("Barista");
                barista.setApellidos("Café");
                barista.setEmail("barista@arysu.pe");
                barista.setPassword(passwordEncoder.encode("barista123"));
                barista.setRol(Rol.BARISTA);
                usuarioRepository.save(barista);
            }

            if (usuarioRepository.findByEmail("cliente@arysu.pe").isEmpty()) {
                Usuario user = new Usuario();
                user.setNombres("Cliente");
                user.setApellidos("Final");
                user.setEmail("cliente@arysu.pe");
                user.setPassword(passwordEncoder.encode("cliente123"));
                user.setRol(Rol.USER);
                usuarioRepository.save(user);
            }

            // Categorías
            if (categoriaRepository.count() == 0) {
                Categoria bebidas = new Categoria("Bebidas");
                Categoria postres = new Categoria("Postres");
                Categoria snacks = new Categoria("Snacks");

                categoriaRepository.saveAll(Arrays.asList(bebidas, postres, snacks));

                // Productos
                productoRepository.save(new Producto("Latte", new BigDecimal("12.50"), "latte.jpg", bebidas));
                productoRepository.save(new Producto("Brownie", new BigDecimal("8.00"), "brownie.jpg", postres));
                productoRepository.save(new Producto("Empanada", new BigDecimal("6.00"), "empanada.jpg", snacks));
            }
        };
    }
}


















