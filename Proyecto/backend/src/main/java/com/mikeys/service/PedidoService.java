package com.mikeys.service;

import com.mikeys.model.*;
import com.mikeys.repository.ItemPedidoRepository;
import com.mikeys.repository.PedidoRepository;
import com.mikeys.repository.ProductoRepository;
import com.mikeys.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de negocio para gestionar pedidos
 * Contiene la lógica de negocio relacionada con pedidos
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                        UsuarioRepository usuarioRepository,
                        ProductoRepository productoRepository,
                        ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    /**
     * Crea un nuevo pedido
     * @param usuarioId ID del usuario que realiza el pedido
     * @param items Lista de items del pedido
     * @return Pedido creado
     * @throws IllegalArgumentException si no hay suficiente stock
     */
    @Transactional
    public Pedido crearPedido(Long usuarioId, List<ItemPedido> items) {
        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        // Validar y actualizar stock de productos
        for (ItemPedido item : items) {
            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + item.getProducto().getId()));
            
            // Verificar stock disponible
            if (producto.getStock() < item.getCantidad()) {
                throw new IllegalArgumentException("No hay suficiente stock del producto: " + producto.getNombre());
            }
            
            // Actualizar el producto en el item con los datos completos
            item.setProducto(producto);
            item.setPrecio(producto.getPrecio());
        }

        // Calcular el total del pedido
        BigDecimal total = calcularTotal(items);

        // Crear el pedido
        Pedido pedido = new Pedido(usuario, total, items);

        // Guardar el pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Actualizar el stock de los productos
        for (ItemPedido item : items) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        return pedidoGuardado;
    }

    /**
     * Busca un pedido por su ID
     * @param id ID del pedido
     * @return Pedido encontrado
     * @throws IllegalArgumentException si no existe el pedido
     */
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los pedidos de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de pedidos del usuario
     */
    public List<Pedido> obtenerPedidosPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
        return pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);
    }

    /**
     * Obtiene todos los pedidos
     * @return Lista de todos los pedidos
     */
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    /**
     * Actualiza el estado de un pedido
     * @param id ID del pedido
     * @param estado Nuevo estado del pedido
     * @return Pedido actualizado
     */
    public Pedido actualizarEstado(Long id, EstadoPedido estado) {
        Pedido pedido = buscarPorId(id);
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }

    /**
     * Cancela un pedido y restaura el stock
     * @param id ID del pedido a cancelar
     * @return Pedido cancelado
     */
    @Transactional
    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        
        // Verificar que el pedido pueda ser cancelado
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede cancelar un pedido ya entregado");
        }
        
        // Restaurar el stock de los productos
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() + item.getCantidad());
            productoRepository.save(producto);
        }
        
        // Cambiar el estado a cancelado
        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    /**
     * Calcula el total de un pedido basado en sus items
     * @param items Lista de items del pedido
     * @return Total del pedido
     */
    private BigDecimal calcularTotal(List<ItemPedido> items) {
        return items.stream()
                .map(item -> item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtiene los items de un pedido
     * @param pedidoId ID del pedido
     * @return Lista de items del pedido
     */
    public List<ItemPedido> obtenerItemsPorPedido(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        return itemPedidoRepository.findByPedido(pedido);
    }

    /**
     * Obtiene pedidos por estado
     * @param estado Estado del pedido
     * @return Lista de pedidos con el estado especificado
     */
    public List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(estado);
    }
}