package com.mikeys.controller;

import com.mikeys.model.*;
import com.mikeys.service.PedidoService;
import com.mikeys.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar pedidos
 * Expone endpoints para CRUD de pedidos
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    @Autowired
    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Crea un nuevo pedido
     * @param orderRequest Datos del pedido a crear
     * @return ResponseEntity con el pedido creado o mensaje de error
     */
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody OrderRequest orderRequest) {
        try {
            // Validar datos del pedido
            if (orderRequest.getUsuarioId() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("El ID del usuario es obligatorio"));
            }
            
            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El pedido debe contener al menos un item"));
            }

            // Convertir OrderItemRequest a ItemPedido
            List<ItemPedido> items = new ArrayList<>();
            for (OrderItemRequest itemRequest : orderRequest.getItems()) {
                if (itemRequest.getProductoId() == null || itemRequest.getCantidad() == null || itemRequest.getCantidad() <= 0) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Datos de item inválidos"));
                }
                
                Producto producto = new Producto();
                producto.setId(itemRequest.getProductoId());
                
                ItemPedido item = new ItemPedido(producto, itemRequest.getCantidad(), BigDecimal.ZERO);
                items.add(item);
            }

            // Crear el pedido
            Pedido pedidoCreado = pedidoService.crearPedido(orderRequest.getUsuarioId(), items);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse("Pedido creado exitosamente", pedidoCreado));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al crear pedido: " + e.getMessage()));
        }
    }

    /**
     * Obtiene todos los pedidos de un usuario
     * @param usuarioId ID del usuario
     * @return ResponseEntity con la lista de pedidos
     */
    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<?> obtenerPedidosPorUsuario(@PathVariable Long usuarioId) {
        try {
            // Verificar que el usuario existe
            usuarioService.buscarPorId(usuarioId);
            
            List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioId);
            
            if (pedidos.isEmpty()) {
                return ResponseEntity.ok(createSuccessResponse("No hay pedidos para este usuario", pedidos));
            }
            
            return ResponseEntity.ok(createSuccessResponse("Pedidos obtenidos exitosamente", pedidos));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    /**
     * Obtiene un pedido por su ID
     * @param id ID del pedido
     * @return ResponseEntity con el pedido o mensaje de error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.buscarPorId(id);
            return ResponseEntity.ok(createSuccessResponse("Pedido encontrado", pedido));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener pedido: " + e.getMessage()));
        }
    }

    /**
     * Actualiza el estado de un pedido
     * @param id ID del pedido
     * @param estadoRequest Nuevo estado del pedido
     * @return ResponseEntity con el pedido actualizado o mensaje de error
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> actualizarEstadoPedido(@PathVariable Long id, @RequestBody EstadoRequest estadoRequest) {
        try {
            if (estadoRequest.getEstado() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("El estado es obligatorio"));
            }

            Pedido pedidoActualizado = pedidoService.actualizarEstado(id, estadoRequest.getEstado());
            return ResponseEntity.ok(createSuccessResponse("Estado del pedido actualizado exitosamente", pedidoActualizado));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar estado del pedido: " + e.getMessage()));
        }
    }

    /**
     * Cancela un pedido
     * @param id ID del pedido a cancelar
     * @return ResponseEntity con mensaje de éxito o error
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido pedidoCancelado = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(createSuccessResponse("Pedido cancelado exitosamente", pedidoCancelado));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al cancelar pedido: " + e.getMessage()));
        }
    }

    /**
     * Clase interna para recibir los datos del pedido
     */
    public static class OrderRequest {
        private Long usuarioId;
        private List<OrderItemRequest> items;

        // Getters y Setters
        public Long getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }

        public List<OrderItemRequest> getItems() {
            return items;
        }

        public void setItems(List<OrderItemRequest> items) {
            this.items = items;
        }
    }

    /**
     * Clase interna para recibir los items del pedido
     */
    public static class OrderItemRequest {
        private Long productoId;
        private Integer cantidad;

        // Getters y Setters
        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    /**
     * Clase interna para recibir el estado del pedido
     */
    public static class EstadoRequest {
        private EstadoPedido estado;

        // Getters y Setters
        public EstadoPedido getEstado() {
            return estado;
        }

        public void setEstado(EstadoPedido estado) {
            this.estado = estado;
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