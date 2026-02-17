// Configuración de la API
const API_URL = 'http://localhost:8080/api';

// Estado de la aplicación
let currentUser = null;
let cart = JSON.parse(localStorage.getItem('cart')) || [];

// Verificar sesión al cargar la página
document.addEventListener('DOMContentLoaded', function() {
    checkSession();
    updateCartUI();
    
    // Cargar productos según la página
    if (document.getElementById('featuredProducts')) {
        loadFeaturedProducts();
    } else if (document.getElementById('productsGrid')) {
        loadAllProducts();
    } else if (document.getElementById('cartItems')) {
        loadCartItems();
    }
    
    // Configurar event listeners
    setupEventListeners();
});

// Verificar sesión de usuario
function checkSession() {
    const token = localStorage.getItem('token');
    if (token) {
        currentUser = JSON.parse(localStorage.getItem('user'));
        updateAuthUI();
    }
}

// Actualizar UI según autenticación
function updateAuthUI() {
    const loginLink = document.getElementById('loginLink');
    const logoutItem = document.getElementById('logoutItem');
    
    if (currentUser) {
        if (loginLink) loginLink.style.display = 'none';
        if (logoutItem) logoutItem.style.display = 'block';
    } else {
        if (loginLink) loginLink.style.display = 'block';
        if (logoutItem) logoutItem.style.display = 'none';
    }
}

// Configurar event listeners
function setupEventListeners() {
    // Formulario de login
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // Formulario de registro
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
    
    // Logout
    const logoutLink = document.getElementById('logoutLink');
    if (logoutLink) {
        logoutLink.addEventListener('click', handleLogout);
    }
    
    // Checkout
    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', handleCheckout);
    }
}

// Manejar login
async function handleLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            currentUser = data.user;
            
            showMessage('Inicio de sesión exitoso', 'success');
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);
        } else {
            showMessage(data.message || 'Error al iniciar sesión', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage('Error de conexión con el servidor', 'error');
    }
}

// Manejar registro
async function handleRegister(e) {
    e.preventDefault();
    
    const nombre = document.getElementById('nombre').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const direccion = document.getElementById('direccion').value;
    const telefono = document.getElementById('telefono').value;
    
    // Validar contraseñas
    if (password !== confirmPassword) {
        showMessage('Las contraseñas no coinciden', 'error');
        return;
    }
    
    // Validar longitud de contraseña
    if (password.length < 6) {
        showMessage('La contraseña debe tener al menos 6 caracteres', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nombre,
                email,
                password,
                direccion,
                telefono
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('Registro exitoso. Redirigiendo al login...', 'success');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else {
            showMessage(data.message || 'Error al registrar usuario', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage('Error de conexión con el servidor', 'error');
    }
}

// Manejar logout
function handleLogout(e) {
    e.preventDefault();
    
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    currentUser = null;
    
    updateAuthUI();
    window.location.href = 'index.html';
}

// Cargar productos destacados
async function loadFeaturedProducts() {
    try {
        const response = await fetch(`${API_URL}/products`);
        const products = await response.json();
        
        const featuredProducts = products.slice(0, 6); // Mostrar solo 6 productos destacados
        displayProducts(featuredProducts, 'featuredProducts');
    } catch (error) {
        console.error('Error cargando productos destacados:', error);
    }
}

// Cargar todos los productos
async function loadAllProducts() {
    try {
        const response = await fetch(`${API_URL}/products`);
        const products = await response.json();
        
        displayProducts(products, 'productsGrid');
    } catch (error) {
        console.error('Error cargando productos:', error);
    }
}

// Mostrar productos en el DOM
function displayProducts(products, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    
    container.innerHTML = products.map(product => `
        <div class="product-card">
            <img src="${product.imagen || 'https://via.placeholder.com/280x200?text=Producto'}" 
                 alt="${product.nombre}" class="product-image">
            <div class="product-info">
                <h3 class="product-title">${product.nombre}</h3>
                <p class="product-description">${product.descripcion}</p>
                <p class="product-price">$${product.precio.toFixed(2)}</p>
                <p class="product-stock">${product.stock > 0 ? `Stock: ${product.stock}` : 'Agotado'}</p>
                <button class="btn-add-cart" 
                        onclick="addToCart(${product.id})" 
                        ${product.stock === 0 ? 'disabled' : ''}>
                    ${product.stock > 0 ? 'Agregar al Carrito' : 'Agotado'}
                </button>
            </div>
        </div>
    `).join('');
}

// Agregar producto al carrito
function addToCart(productId) {
    // Verificar si el usuario está logueado
    if (!currentUser) {
        showMessage('Debes iniciar sesión para agregar productos al carrito', 'error');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1500);
        return;
    }
    
    // Buscar el producto
    fetch(`${API_URL}/products`)
        .then(response => response.json())
        .then(products => {
            const product = products.find(p => p.id === productId);
            if (product && product.stock > 0) {
                // Verificar si ya está en el carrito
                const existingItem = cart.find(item => item.id === productId);
                
                if (existingItem) {
                    if (existingItem.quantity < product.stock) {
                        existingItem.quantity += 1;
                    } else {
                        showMessage('No hay suficiente stock disponible', 'error');
                        return;
                    }
                } else {
                    cart.push({
                        id: product.id,
                        nombre: product.nombre,
                        precio: product.precio,
                        imagen: product.imagen,
                        quantity: 1
                    });
                }
                
                localStorage.setItem('cart', JSON.stringify(cart));
                updateCartUI();
                showMessage('Producto agregado al carrito', 'success');
            } else {
                showMessage('Producto agotado', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showMessage('Error al agregar producto al carrito', 'error');
        });
}

// Actualizar UI del carrito
function updateCartUI() {
    // Actualizar contador del carrito si existe
    const cartCountElements = document.querySelectorAll('.cart-count');
    cartCountElements.forEach(element => {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        element.textContent = totalItems;
    });
}

// Cargar items del carrito
function loadCartItems() {
    const cartItemsContainer = document.getElementById('cartItems');
    const emptyCart = document.getElementById('emptyCart');
    const cartContent = document.getElementById('cartContent');
    
    if (cart.length === 0) {
        if (emptyCart) emptyCart.style.display = 'block';
        if (cartContent) cartContent.style.display = 'none';
        return;
    }
    
    if (emptyCart) emptyCart.style.display = 'none';
    if (cartContent) cartContent.style.display = 'grid';
    
    if (cartItemsContainer) {
        cartItemsContainer.innerHTML = cart.map(item => `
            <div class="cart-item">
                <img src="${item.imagen || 'https://via.placeholder.com/80x80?text=Producto'}" 
                     alt="${item.nombre}" class="cart-item-image">
                <div class="cart-item-info">
                    <h3 class="cart-item-title">${item.nombre}</h3>
                    <p class="cart-item-price">$${item.precio.toFixed(2)}</p>
                    <div class="cart-item-quantity">
                        <button class="quantity-btn" onclick="updateQuantity(${item.id}, ${item.quantity - 1})">-</button>
                        <input type="number" class="quantity-input" value="${item.quantity}" 
                               onchange="updateQuantity(${item.id}, this.value)">
                        <button class="quantity-btn" onclick="updateQuantity(${item.id}, ${item.quantity + 1})">+</button>
                    </div>
                </div>
                <button class="btn-remove" onclick="removeFromCart(${item.id})">Eliminar</button>
            </div>
        `).join('');
    }
    
    updateCartSummary();
}

// Actualizar cantidad de producto
function updateQuantity(productId, newQuantity) {
    newQuantity = parseInt(newQuantity);
    
    if (newQuantity <= 0) {
        removeFromCart(productId);
        return;
    }
    
    const item = cart.find(item => item.id === productId);
    if (item) {
        // Verificar stock
        fetch(`${API_URL}/products`)
            .then(response => response.json())
            .then(products => {
                const product = products.find(p => p.id === productId);
                if (product && newQuantity <= product.stock) {
                    item.quantity = newQuantity;
                    localStorage.setItem('cart', JSON.stringify(cart));
                    updateCartUI();
                    loadCartItems();
                } else {
                    showMessage('No hay suficiente stock disponible', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('Error al actualizar cantidad', 'error');
            });
    }
}

// Eliminar producto del carrito
function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartUI();
    loadCartItems();
    showMessage('Producto eliminado del carrito', 'success');
}

// Actualizar resumen del carrito
function updateCartSummary() {
    const subtotal = cart.reduce((sum, item) => sum + (item.precio * item.quantity), 0);
    const shipping = subtotal > 0 ? 50.00 : 0; // Envío gratis si el carrito está vacío o $50
    const total = subtotal + shipping;
    
    const subtotalElement = document.getElementById('subtotal');
    const shippingElement = document.getElementById('shipping');
    const totalElement = document.getElementById('total');
    
    if (subtotalElement) subtotalElement.textContent = `$${subtotal.toFixed(2)}`;
    if (shippingElement) shippingElement.textContent = shipping === 0 ? 'Gratis' : `$${shipping.toFixed(2)}`;
    if (totalElement) totalElement.textContent = `$${total.toFixed(2)}`;
}

// Manejar checkout
async function handleCheckout() {
    if (!currentUser) {
        showMessage('Debes iniciar sesión para realizar un pedido', 'error');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1500);
        return;
    }
    
    if (cart.length === 0) {
        showMessage('Tu carrito está vacío', 'error');
        return;
    }
    
    const orderData = {
        usuarioId: currentUser.id,
        items: cart.map(item => ({
            productoId: item.id,
            cantidad: item.quantity,
            precio: item.precio
        }))
    };
    
    try {
        const response = await fetch(`${API_URL}/orders`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(orderData)
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('Pedido realizado exitosamente', 'success');
            
            // Vaciar carrito
            cart = [];
            localStorage.setItem('cart', JSON.stringify(cart));
            updateCartUI();
            
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 2000);
        } else {
            showMessage(data.message || 'Error al realizar el pedido', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage('Error de conexión con el servidor', 'error');
    }
}

// Mostrar mensaje
function showMessage(text, type) {
    const messageElement = document.getElementById('message');
    if (messageElement) {
        messageElement.textContent = text;
        messageElement.className = `message ${type}`;
        messageElement.style.display = 'block';
        
        setTimeout(() => {
            messageElement.style.display = 'none';
        }, 5000);
    } else {
        // Si no hay elemento de mensaje específico, usar alert
        alert(text);
    }
}

// Función para formatear precios
function formatPrice(price) {
    return `$${price.toFixed(2)}`;
}

// Función para obtener headers con autenticación
function getAuthHeaders() {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    const token = localStorage.getItem('token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    return headers;
}