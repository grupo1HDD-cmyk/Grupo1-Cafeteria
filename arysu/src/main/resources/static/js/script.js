/**
 * FUNCIONALIDADES BÁSICAS PARA AROMA Y SUEÑOS
 * - Validación de formularios
 * - Interacción del carrito
 * - Mostrar/ocultar contraseñas
 */

document.addEventListener('DOMContentLoaded', function() {

    // Asegúrate de que window.appContextPath esté definido globalmente,
    // por ejemplo, inyectado por Thymeleaf en un <script> en tu layout.html:
    // <script th:inline="javascript">
    //     var appContextPath = /*[[@{/}]]*/ '';
    // </script>
    if (typeof window.appContextPath === 'undefined') {
        console.error('Error: window.appContextPath no está definido. Asegúrate de inyectarlo desde Thymeleaf.');
        // Fallback para desarrollo si no está inyectado, pero NO para producción
        window.appContextPath = '/aromaysuenos/'; // O tu ruta base por defecto
    }

    const baseUrlMenu = window.appContextPath + 'menu'; 

    // ==================== MOSTRAR/OCULTAR CONTRASEÑA ====================
    document.querySelectorAll('.toggle-password').forEach(button => {
        button.addEventListener('click', function() {
            const input = this.previousElementSibling;
            const icon = this.querySelector('i');
            if (input.type === "password") {
                input.type = "text";
                // Usar classList.toggle o simplemente alternar
                icon.classList.remove('fa-eye', 'bi-eye');
                icon.classList.add('fa-eye-slash', 'bi-eye-slash'); 
            } else {
                input.type = "password";
                icon.classList.remove('fa-eye-slash', 'bi-eye-slash');
                icon.classList.add('fa-eye', 'bi-eye'); 
            }
        });
    });

    // ==================== VALIDACIÓN DE FORMULARIOS ====================
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function(e) {
            const password = this.querySelector('#password');
            const confirmPassword = this.querySelector('#confirmPassword');
            if (password && confirmPassword && password.value !== confirmPassword.value) {
                e.preventDefault();
                alert('⚠️ Las contraseñas no coinciden');
                return;
            }

            const fechaInput = this.querySelector('[type="date"]');
            if (fechaInput) {
                const fecha = new Date(fechaInput.value + 'T00:00:00'); // Asegura la zona horaria para comparación
                const hoy = new Date();
                hoy.setHours(0, 0, 0, 0); // Establece la hora a 00:00:00 para comparar solo la fecha

                if (fecha > hoy) {
                    e.preventDefault();
                    alert('⚠️ La fecha no puede ser futura');
                }
            }
        });
    });


    // ==================== CARRITO DE COMPRAS - LÓGICA COMPLETA ====================
    const carritoContadorSpan = document.getElementById('carrito-contador'); 
    const carritoTotalSpan = document.getElementById('carrito-total');       
    const cartItemsContainer = document.getElementById('cart-items-container'); 
    const emptyCartMessage = document.getElementById('empty-cart-message');     
    const cartModalTotalSpan = document.getElementById('cart-total');       
    const checkoutButton = document.getElementById('checkout-button');         

    let cart = []; 

    function loadCart() {
        const storedCart = localStorage.getItem('aromaysuenos_cart');
        if (storedCart) {
            cart = JSON.parse(storedCart);
            // Asegurarse de que el precio sea numérico después de cargarlo de localStorage
            cart.forEach(item => {
                item.precio = parseFloat(item.precio); 
            });
        }
        updateCartDisplay(); 
    }

    function saveCart() {
        localStorage.setItem('aromaysuenos_cart', JSON.stringify(cart));
    }

    function updateCartDisplay() {
        if (cartItemsContainer) { 
            cartItemsContainer.innerHTML = ''; 
        }

        let total = 0;
        let itemCount = 0;

        if (emptyCartMessage) {
            if (cart.length === 0) {
                emptyCartMessage.style.display = 'block';
            } else {
                emptyCartMessage.style.display = 'none';
            }
        }

        cart.forEach(item => {
            const itemTotalPrice = item.precio * item.cantidad; // Calcula el total del ítem
            if (cartItemsContainer) { 
                const itemElement = document.createElement('div');
                itemElement.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'py-2', 'border-bottom');
                itemElement.innerHTML = `
                    <div>
                        <span class="fw-bold">${item.nombre}</span>
                        <br>
                        <small class="text-muted">Cantidad: ${item.cantidad}</small>
                    </div>
                    <div>
                        <span>S/ ${itemTotalPrice.toFixed(2)}</span>
                        <button class="btn btn-sm btn-danger ms-2 remove-item-btn" data-id="${item.id}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                `;
                cartItemsContainer.appendChild(itemElement);
            }
            total += itemTotalPrice;
            itemCount += item.cantidad;
        });

        if (carritoContadorSpan) carritoContadorSpan.textContent = itemCount;
        if (carritoTotalSpan) carritoTotalSpan.textContent = `S/ ${total.toFixed(2)}`;
        if (cartModalTotalSpan) cartModalTotalSpan.textContent = `S/ ${total.toFixed(2)}`;
    }

    // Event listener para añadir productos (Delegación de eventos)
    document.body.addEventListener('click', function(event) {
        const button = event.target.closest('.btn-agregar-carrito');
        if (button) {
            console.log('Botón "Añadir" clickeado (delegación):', button.dataset.nombre);

            // Asegúrate de que id sea un número para coincidir con Long en Java, si es necesario.
            // Aunque JSON.stringify enviará el tipo correcto si es string o number.
            const id = button.dataset.id; // Asume que id es string, lo cual está bien para JSON
            const nombre = button.dataset.nombre;
            // Asegúrate de que el precio sea parseFloat para evitar problemas de tipo
            const precio = parseFloat(button.dataset.precio); 

            const existingItem = cart.find(item => item.id === id); // Comparación de string

            if (existingItem) {
                existingItem.cantidad++;
            } else {
                // Almacenar el precio como número en el objeto de JS
                cart.push({ id, nombre, precio, cantidad: 1 });
            }

            saveCart();
            updateCartDisplay();
            alert(`"${nombre}" añadido al carrito.`); 
        }
    });

    // Event listener para eliminar productos del carrito (delegación de eventos)
    if (cartItemsContainer) { 
        cartItemsContainer.addEventListener('click', function(event) {
            if (event.target.classList.contains('remove-item-btn') || event.target.closest('.remove-item-btn')) {
                const button = event.target.closest('.remove-item-btn');
                const idToRemove = button.dataset.id; // ID del producto a eliminar

                cart = cart.filter(item => item.id !== idToRemove);

                saveCart();
                updateCartDisplay();
            }
        });
    }

    // Event listener para el botón "Proceder al Pago"
    if (checkoutButton) {
        checkoutButton.addEventListener('click', async function() { 
            if (cart.length === 0) {
                alert('Tu carrito está vacío. Por favor, añade productos antes de proceder al pago.');
                return;
            }

            console.log('Enviando carrito al backend para checkout...');

            const postUrl = window.appContextPath + 'carrito/confirmar-pedido';
            console.log('URL de POST a backend:', postUrl);

            try {
                const response = await fetch(postUrl, { 
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    // ¡Importante! JSON.stringify convierte números de JS a números en JSON,
                    // lo cual es correcto para que Spring los mapee a BigDecimal.
                    body: JSON.stringify(cart) 
                });

                if (response.ok) {
                    console.log('Carrito enviado con éxito. Redirigiendo al checkout.');
                    const redirectUrl = window.location.origin + window.appContextPath + 'carrito/checkout';
                    console.log('URL de Redirección:', redirectUrl);
                    window.location.href = redirectUrl;

                } else if (response.status === 401 || response.status === 403) {
                    console.warn('Autenticación requerida para proceder al pago. Redirigiendo al login.');
                    window.location.href = window.location.origin + window.appContextPath + 'auth/login'; 
                } else {
                    // Si hay un error del backend (ej. producto no encontrado)
                    const errorText = await response.text();
                    console.error('Error al enviar el carrito al backend:', response.status, errorText);
                    alert('Hubo un error al preparar tu pedido: ' + errorText + '. Por favor, inténtalo de nuevo.');
                }
            } catch (error) {
                console.error('Error de red al enviar el carrito:', error);
                alert('No se pudo conectar con el servidor para preparar el pedido.');
            }
        });
    }

    // Cargar el carrito al inicio
    loadCart();

    // ==================== LÓGICA DE FILTRO DE MENÚ ====================
    const categoryButtons = document.querySelectorAll('.btn-group button');

    categoryButtons.forEach(button => {
        button.addEventListener('click', function() {
            const categoryId = this.dataset.categoryId;
            let url = baseUrlMenu; 

            if (categoryId !== 'all') {
                url += '?categoriaId=' + categoryId;
            }
            window.location.href = url;
        });
    });

}); // Fin de DOMContentLoaded