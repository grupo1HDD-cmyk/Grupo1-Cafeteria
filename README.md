### Descripción del proyecto ☕

El proyecto "Aroma y Sueños" es un sistema web para una cafetería ficticia, creado como parte de un curso de **Herramientas de Desarrollo**. Su objetivo es simular las funcionalidades de una cafetería real, permitiendo la gestión de productos, usuarios y pedidos. El desarrollo sigue el patrón de diseño **Modelo-Vista-Controlador (MVC)** y utiliza una arquitectura moderna basada en **Spring Boot**, que facilita la integración entre el backend y el frontend.

---
### Características principales ✨

* **Página web con diversas secciones:** Incluye páginas para Inicio, Menú de productos, Contacto, Nosotros, Registro e Inicio de sesión, permitiendo una navegación fluida.
* **Operaciones CRUD completas:** Permite realizar operaciones de **Crear, Leer, Actualizar y Eliminar** en las entidades principales de la base de datos, como productos y usuarios.
* **Autenticación y autorización por roles:** Implementa **Spring Security** para gestionar el acceso de los usuarios según sus roles (ADMIN, BARISTA, USER), garantizando una navegación segura.
* **Validaciones de formularios:** Utiliza **Spring Validator** y anotaciones como `@NotBlank`, `@Email`, `@Size` y `@Min` para asegurar la integridad de los datos en los formularios de registro, inicio de sesión y contacto.
* **Base de datos relacional:** Se conecta a una base de datos **MySQL**, con un diseño bien estructurado que incluye tablas como `productos` y `usuarios`, y relaciones de clave foránea entre ellas.
* **Gestión de pedidos:** Permite a los usuarios realizar pedidos que se pueden gestionar y actualizar desde un panel de administrador.

---
### Tecnologías utilizadas 🛠️

* **Spring Boot:** Framework para construir aplicaciones web modernas en Java.
* **Thymeleaf:** Motor de plantillas para la generación de páginas HTML dinámicas conectadas con datos del backend.
* **Spring Data JPA y Hibernate:** Módulos de Spring que simplifican el acceso a bases de datos relacionales con mapeo objeto-relacional (ORM).
* **Spring Security:** Framework de seguridad robusto y personalizable para la autenticación y autorización.
* **Spring Boot DevTools:** Herramienta que agiliza el desarrollo al permitir recargas automáticas del servidor.
* **MySQL:** Sistema de gestión de bases de datos relacionales.
* **Lombok:** Biblioteca de Java que reduce la escritura de código repetitivo.
* **Bootstrap:** Framework de estilos CSS para diseño responsivo y componentes visuales.

---
### Estructura del proyecto 📁

El proyecto sigue una estructura limpia y escalable basada en el patrón MVC:

* **Entidades (Entities):** Clases que representan los modelos de datos de la base de datos.
* **Repositorios (Repositories):** Interfaces que extienden `JpaRepository` para la gestión de datos.
* **Servicios (Services):** Contienen la lógica de negocio del sistema.
* **Controladores (Controllers):** Gestionan las peticiones del usuario y la interacción con las vistas.

---
### Capturas del programa 📸

| Página principal | Menú y carrito de compras |
| --- | --- |
| ![Página de inicio](https://i.imgur.com/l6DuKOO.png,) | ![Página del menú y carrito](https://i.imgur.com/nbyvgW4.png) |

| Confirmación de compra | Inicio de sesión y panel de administrador |
| --- | --- |
| ![Página de confirmación](https://i.imgur.com/rroIBbx.png) | ![Página de inicio de sesión y panel de administrador](https://i.imgur.com/FUmHWuZ.png) |

| Gestión de productos y pedidos | Página de error |
| --- | --- |
| ![Página de gestión de productos y pedidos](URL_DE_LA_IMAGEN_5) | ![Página de error 404](https://i.imgur.com/ZxwAqE0.png) |
