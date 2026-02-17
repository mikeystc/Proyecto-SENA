# Mikeys - Tienda Online

Una aplicación web completa de comercio electrónico desarrollada con tecnologías modernas de frontend y backend.

## 📋 Descripción del Proyecto

Mikeys es una tienda online totalmente funcional que permite a los usuarios:
- Registrarse y autenticarse en el sistema
- Explorar el catálogo de productos
- Agregar productos al carrito de compras
- Realizar pedidos
- Gestionar sus pedidos anteriores

## 🛠 Tecnologías Utilizadas

### Frontend
- **HTML5** - Estructura de las páginas web
- **CSS3** - Estilos y diseño responsivo
- **JavaScript (ES6+)** - Lógica del cliente y consumo de APIs

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.1.5** - Framework de aplicaciones
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - Creación de APIs REST
- **Maven** - Gestión de dependencias y construcción

### Base de Datos
- **MySQL 8.0+** - Sistema de gestión de bases de datos

## 📁 Estructura del Proyecto

```
mikeys/
│
├── frontend/                     # Código del frontend
│   ├── index.html               # Página principal
│   ├── login.html               # Formulario de inicio de sesión
│   ├── register.html            # Formulario de registro
│   ├── productos.html           # Catálogo de productos
│   ├── carrito.html             # Carrito de compras
│   ├── css/
│   │   └── styles.css           # Estilos de la aplicación
│   └── js/
│       └── main.js              # Lógica del frontend
│
├── backend/                     # Código del backend
│   ├── src/main/java/com/mikeys/
│   │   ├── controller/          # Controladores REST
│   │   │   ├── AuthController.java
│   │   │   ├── ProductoController.java
│   │   │   └── PedidoController.java
│   │   ├── service/             # Lógica de negocio
│   │   │   ├── UsuarioService.java
│   │   │   ├── ProductoService.java
│   │   │   └── PedidoService.java
│   │   ├── repository/          # Repositorios JPA
│   │   │   ├── UsuarioRepository.java
│   │   │   ├── ProductoRepository.java
│   │   │   ├── PedidoRepository.java
│   │   │   └── ItemPedidoRepository.java
│   │   ├── model/               # Entidades JPA
│   │   │   ├── Usuario.java
│   │   │   ├── Producto.java
│   │   │   ├── Pedido.java
│   │   │   ├── ItemPedido.java
│   │   │   └── EstadoPedido.java
│   │   └── MikeysApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── database/
│   └── mikeys.sql               # Script de creación de base de datos
│
├── README.md                    # Documentación del proyecto
└── .gitignore                   # Archivos a ignorar en Git
```

## 🚀 Instalación y Configuración

### Requisitos Previos

- Java JDK 17 o superior
- Maven 3.6 o superior
- MySQL 8.0 o superior
- Navegador web moderno (Chrome, Firefox, Safari, Edge)

### Paso 1: Configurar la Base de Datos

1. Inicia tu servidor MySQL
2. Ejecuta el script SQL ubicado en `database/mikeys.sql`:
   ```bash
   mysql -u root -p < database/mikeys.sql
   ```
   
3. El script creará:
   - Base de datos `mikeys`
   - Tablas necesarias (usuarios, productos, pedidos, items_pedido)
   - Usuarios de prueba
   - 15 productos de ejemplo

### Paso 2: Configurar el Backend

1. Navega al directorio del backend:
   ```bash
   cd backend
   ```

2. Verifica la configuración en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mikeys?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=password
   ```
   
   Ajusta el usuario y contraseña según tu configuración de MySQL.

3. Compila y ejecuta el proyecto con Maven:
   ```bash
   mvn spring-boot:run
   ```
   
   El servidor backend estará disponible en `http://localhost:8080`

### Paso 3: Ejecutar el Frontend

1. Abre el archivo `frontend/index.html` en tu navegador
2. O sirve los archivos estáticos con un servidor local:
   ```bash
   cd frontend
   python -m http.server 8081
   ```
   
   El frontend estará disponible en `http://localhost:8081`

## 📡 Endpoints de la API

### Autenticación

#### Registro de Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "direccion": "Calle Principal 123",
  "telefono": "555-0101"
}
```

#### Inicio de Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```

### Productos

#### Obtener Todos los Productos
```http
GET /api/products
```

#### Obtener Producto por ID
```http
GET /api/products/{id}
```

#### Buscar Productos por Nombre
```http
GET /api/products/search?nombre=laptop
```

#### Obtener Productos Disponibles
```http
GET /api/products/available
```

### Pedidos

#### Crear Pedido
```http
POST /api/orders
Content-Type: application/json

{
  "usuarioId": 1,
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 3,
      "cantidad": 1
    }
  ]
}
```

#### Obtener Pedidos de un Usuario
```http
GET /api/orders/user/{usuarioId}
```

#### Obtener Pedido por ID
```http
GET /api/orders/{id}
```

#### Actualizar Estado del Pedido
```http
PUT /api/orders/{id}/status
Content-Type: application/json

{
  "estado": "PROCESANDO"
}
```

#### Cancelar Pedido
```http
PUT /api/orders/{id}/cancel
```

## 👤 Usuarios de Prueba

El sistema incluye los siguientes usuarios de prueba:

| Email | Contraseña | Nombre |
|-------|------------|--------|
| juan@example.com | password123 | Juan Pérez |
| maria@example.com | password123 | María García |
| carlos@example.com | password123 | Carlos López |

## 🛍 Productos Disponibles

El catálogo incluye 15 productos de tecnología, como:
- Laptops y tablets
- Smartphones y accesorios
- Auriculares y altavoces
- Cámaras digitales
- Componentes de PC
- Dispositivos de almacenamiento
- Y más...

## 🔧 Funcionalidades Implementadas

### Frontend
- ✅ Navegación entre páginas
- ✅ Formularios de registro y login
- ✅ Catálogo de productos con grid responsivo
- ✅ Carrito de compras persistente (LocalStorage)
- ✅ Gestión de cantidades en el carrito
- ✅ Cálculo automático de totales
- ✅ Proceso de checkout
- ✅ Diseño responsivo para móviles y desktop
- ✅ Manejo de errores y mensajes al usuario

### Backend
- ✅ API REST completa
- ✅ Autenticación de usuarios
- ✅ CRUD de productos
- ✅ Gestión de pedidos y carritos
- ✅ Validación de datos
- ✅ Manejo de transacciones
- ✅ Control de stock de productos
- ✅ CORS configurado
- ✅ Documentación de código

### Base de Datos
- ✅ Esquema relacional normalizado
- ✅ Relaciones entre tablas
- ✅ Índices para optimización
- ✅ Datos de prueba incluidos
- ✅ Constraints y validaciones

## 🎨 Diseño y Experiencia de Usuario

El frontend utiliza un diseño moderno con:
- Paleta de colores profesional (azul y gris)
- Tipografía limpia y legible
- Layout responsivo que se adapta a diferentes dispositivos
- Animaciones suaves en hover
- Feedback visual para acciones del usuario
- Mensajes de error y éxito claros

## 🔒 Consideraciones de Seguridad

Este es un proyecto educativo. En un entorno de producción se deberían implementar:

- Encriptación de contraseñas (BCrypt)
- Autenticación con JWT tokens
- Validación y sanitización de inputs
- Protección CSRF
- Rate limiting
- HTTPS obligatorio
- Logs de auditoría

## 🚀 Escalabilidad y Mejoras Futuras

Posibles mejoras para el proyecto:

### Funcionalidades
- ✅ Sistema de reseñas y calificaciones de productos
- ✅ Wishlist/Favoritos
- ✅ Historial de pedidos detallado
- ✅ Gestión de direcciones de envío múltiples
- ✅ Sistema de cupones de descuento
- ✅ Integración con pasarelas de pago reales
- ✅ Envío de correos electrónicos
- ✅ Panel de administración

### Técnicas
- ✅ Implementación de caché (Redis)
- ✅ Paginación en listados
- ✅ Búsqueda avanzada y filtros
- ✅ API de recomendaciones
- ✅ Tests unitarios y de integración
- ✅ Dockerización del proyecto
- ✅ CI/CD pipeline

## 📝 Licencia

Este proyecto es de código abierto y está disponible bajo la Licencia MIT.

## 👨‍💻 Autor

Desarrollado como proyecto educativo de una tienda web completa con arquitectura cliente-servidor.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Siéntete libre de:
- Reportar bugs
- Sugerir nuevas funcionalidades
- Mejorar la documentación
- Enviar pull requests

---

**¡Gracias por usar Mikeys! 🛒✨**