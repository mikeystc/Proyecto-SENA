-- ============================================
-- Script SQL para la Base de Datos de Mikeys
-- Tienda Online
-- ============================================

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS mikeys 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_general_ci;

-- Usar la base de datos
USE mikeys;

-- ============================================
-- Tabla: usuarios
-- Almacena la información de los usuarios del sistema
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Tabla: productos
-- Almacena la información de los productos disponibles
-- ============================================
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen VARCHAR(255),
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Tabla: pedidos
-- Almacena los pedidos realizados por los usuarios
-- ============================================
CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    fecha_pedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL,
    estado ENUM('PENDIENTE', 'PROCESANDO', 'ENVIADO', 'ENTREGADO', 'CANCELADO') 
        NOT NULL DEFAULT 'PENDIENTE',
    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Tabla: items_pedido
-- Almacena los items de cada pedido
-- ============================================
CREATE TABLE IF NOT EXISTS items_pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- Índices para mejorar el rendimiento
-- ============================================

-- Índice para búsqueda rápida de usuarios por email
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);

-- Índice para búsqueda rápida de productos por nombre
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);

-- Índice para búsqueda rápida de pedidos por usuario
CREATE INDEX IF NOT EXISTS idx_pedidos_usuario_id ON pedidos(usuario_id);

-- Índice para búsqueda rápida de pedidos por estado
CREATE INDEX IF NOT EXISTS idx_pedidos_estado ON pedidos(estado);

-- Índice para búsqueda rápida de items por pedido
CREATE INDEX IF NOT EXISTS idx_items_pedido_pedido_id ON items_pedido(pedido_id);

-- ============================================
-- Inserción de datos de prueba
-- ============================================

-- Insertar usuarios de prueba
-- Contraseñas en texto plano (en producción deberían estar encriptadas)
INSERT INTO usuarios (nombre, email, password, direccion, telefono) VALUES
('Juan Pérez', 'juan@example.com', 'password123', 'Calle Principal 123, Ciudad', '555-0101'),
('María García', 'maria@example.com', 'password123', 'Avenida Central 456, Ciudad', '555-0102'),
('Carlos López', 'carlos@example.com', 'password123', 'Calle Secundaria 789, Ciudad', '555-0103');

-- Insertar productos de prueba
INSERT INTO productos (nombre, descripcion, precio, stock, imagen) VALUES
('Laptop Gamer Pro', 'Laptop de alto rendimiento para gaming con procesador Intel i7, 16GB RAM y SSD 512GB', 1299.99, 10, 'https://via.placeholder.com/300x200?text=Laptop+Gamer'),
('Smartphone XZ Premium', 'Smartphone de última generación con cámara de 108MP y pantalla AMOLED de 6.7 pulgadas', 799.99, 25, 'https://via.placeholder.com/300x200?text=Smartphone'),
('Auriculares Bluetooth', 'Auriculares inalámbricos con cancelación de ruido activa y batería de 30 horas', 149.99, 50, 'https://via.placeholder.com/300x200?text=Auriculares'),
('Smartwatch Fitness', 'Reloj inteligente con monitor de actividad física, GPS y resistencia al agua', 299.99, 30, 'https://via.placeholder.com/300x200?text=Smartwatch'),
('Tablet Pro 12', 'Tablet de alta gama con pantalla de 12 pulgadas, stylus incluido y 256GB de almacenamiento', 649.99, 15, 'https://via.placeholder.com/300x200?text=Tablet'),
('Cámara Digital 4K', 'Cámara digital con grabación en 4K, zoom óptico 10x y estabilizador de imagen', 899.99, 8, 'https://via.placeholder.com/300x200?text=Camara'),
('Teclado Mecánico RGB', 'Teclado mecánico con switches azules y retroiluminación RGB personalizable', 129.99, 40, 'https://via.placeholder.com/300x200?text=Teclado'),
('Mouse Gaming', 'Mouse para gaming con sensor de 16000 DPI y 11 botones programables', 79.99, 60, 'https://via.placeholder.com/300x200?text=Mouse'),
('Monitor 27" 4K', 'Monitor de 27 pulgadas con resolución 4K, 144Hz y tecnología HDR', 599.99, 12, 'https://via.placeholder.com/300x200?text=Monitor'),
('Impresora Multifuncional', 'Impresora láser multifuncional con WiFi, escáner y fax integrado', 249.99, 20, 'https://via.placeholder.com/300x200?text=Impresora'),
('Router WiFi 6', 'Router de última generación con tecnología WiFi 6 y cobertura de hasta 300m²', 199.99, 35, 'https://via.placeholder.com/300x200?text=Router'),
('Disco Duro Externo 2TB', 'Disco duro externo portátil de 2TB con conexión USB 3.0', 89.99, 45, 'https://via.placeholder.com/300x200?text=Disco+Duro'),
('Memoria USB 128GB', 'Memoria flash USB de 128GB con diseño compacto y resistente', 29.99, 100, 'https://via.placeholder.com/300x200?text=USB'),
('Batería Portátil 20000mAh', 'Power bank de 20000mAh con carga rápida y 3 puertos USB', 49.99, 70, 'https://via.placeholder.com/300x200?text=PowerBank'),
('Altavoz Bluetooth', 'Altavoz portátil Bluetooth con sonido estéreo de alta calidad y batería de 12 horas', 99.99, 55, 'https://via.placeholder.com/300x200?text=Altavoz');

-- ============================================
-- Verificación de la estructura
-- ============================================

-- Verificar tablas creadas
SHOW TABLES;

-- Verificar usuarios insertados
SELECT 'Usuarios:' AS Tabla, COUNT(*) AS Total FROM usuarios
UNION ALL
SELECT 'Productos:' AS Tabla, COUNT(*) AS Total FROM productos
UNION ALL
SELECT 'Pedidos:' AS Tabla, COUNT(*) AS Total FROM pedidos
UNION ALL
SELECT 'Items de Pedido:' AS Tabla, COUNT(*) AS Total FROM items_pedido;

-- ============================================
-- Fin del script
-- ============================================