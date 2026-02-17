package com.mikeys.controller;

import com.mikeys.model.Producto;
import com.mikeys.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar productos
 * Expone endpoints para CRUD de productos
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Obtiene todos los productos
     * @return ResponseEntity con la lista de productos
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosProductos() {
        try {
            List<Producto> productos = productoService.obtenerTodos();
            
            if (productos.isEmpty()) {
                return ResponseEntity.ok(createSuccessResponse("No hay productos disponibles", productos));
            }
            
            return ResponseEntity.ok(createSuccessResponse("Productos obtenidos exitosamente", productos));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener productos: " + e.getMessage()));
        }
    }

    /**
     * Obtiene un producto por su ID
     * @param id ID del producto
     * @return ResponseEntity con el producto o mensaje de error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Producto producto = productoService.buscarPorId(id);
            return ResponseEntity.ok(createSuccessResponse("Producto encontrado", producto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener producto: " + e.getMessage()));
        }
    }

    /**
     * Crea un nuevo producto
     * @param producto Datos del producto a crear
     * @return ResponseEntity con el producto creado o mensaje de error
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        try {
            // Validar datos del producto
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El nombre del producto es obligatorio"));
            }
            
            if (producto.getDescripcion() == null || producto.getDescripcion().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La descripción del producto es obligatoria"));
            }
            
            if (producto.getPrecio() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("El precio del producto es obligatorio"));
            }
            
            if (producto.getStock() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("El stock del producto es obligatorio"));
            }

            Producto productoCreado = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse("Producto creado exitosamente", productoCreado));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al crear producto: " + e.getMessage()));
        }
    }

    /**
     * Actualiza un producto existente
     * @param id ID del producto a actualizar
     * @param producto Datos actualizados del producto
     * @return ResponseEntity con el producto actualizado o mensaje de error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(createSuccessResponse("Producto actualizado exitosamente", productoActualizado));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar producto: " + e.getMessage()));
        }
    }

    /**
     * Elimina un producto
     * @param id ID del producto a eliminar
     * @return ResponseEntity con mensaje de éxito o error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok(createSuccessResponse("Producto eliminado exitosamente", null));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al eliminar producto: " + e.getMessage()));
        }
    }

    /**
     * Busca productos por nombre
     * @param nombre Nombre o parte del nombre del producto
     * @return ResponseEntity con la lista de productos encontrados
     */
    @GetMapping("/search")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String nombre) {
        try {
            List<Producto> productos = productoService.buscarPorNombre(nombre);
            
            if (productos.isEmpty()) {
                return ResponseEntity.ok(createSuccessResponse("No se encontraron productos", productos));
            }
            
            return ResponseEntity.ok(createSuccessResponse("Productos encontrados", productos));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al buscar productos: " + e.getMessage()));
        }
    }

    /**
     * Obtiene productos disponibles (con stock > 0)
     * @return ResponseEntity con la lista de productos disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<?> obtenerProductosDisponibles() {
        try {
            List<Producto> productos = productoService.obtenerProductosDisponibles();
            
            if (productos.isEmpty()) {
                return ResponseEntity.ok(createSuccessResponse("No hay productos disponibles", productos));
            }
            
            return ResponseEntity.ok(createSuccessResponse("Productos disponibles obtenidos exitosamente", productos));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener productos disponibles: " + e.getMessage()));
        }
    }

    /**
     * Crea una respuesta de error estándar
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    /**
     * Crea una respuesta exitosa estándar
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}