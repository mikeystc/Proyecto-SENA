package com.mikeys.service;

import com.mikeys.model.Producto;
import com.mikeys.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de negocio para gestionar productos
 * Contiene la lógica de negocio relacionada con productos
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Crea un nuevo producto en el sistema
     * @param producto Producto a crear
     * @return Producto creado
     */
    public Producto crearProducto(Producto producto) {
        // Validar que el precio sea positivo
        if (producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        // Validar que el stock no sea negativo
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        return productoRepository.save(producto);
    }

    /**
     * Busca un producto por su ID
     * @param id ID del producto
     * @return Producto encontrado
     * @throws IllegalArgumentException si no existe el producto
     */
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los productos
     * @return Lista de todos los productos
     */
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    /**
     * Obtiene todos los productos disponibles (con stock > 0)
     * @return Lista de productos disponibles
     */
    public List<Producto> obtenerProductosDisponibles() {
        return productoRepository.findByStockGreaterThan(0);
    }

    /**
     * Busca productos por nombre
     * @param nombre Nombre o parte del nombre del producto
     * @return Lista de productos que coinciden
     */
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Actualiza un producto existente
     * @param id ID del producto a actualizar
     * @param producto Datos actualizados del producto
     * @return Producto actualizado
     */
    public Producto actualizarProducto(Long id, Producto producto) {
        Producto productoExistente = buscarPorId(id);
        
        // Actualizar campos
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setDescripcion(producto.getDescripcion());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setStock(producto.getStock());
        productoExistente.setImagen(producto.getImagen());
        
        return productoRepository.save(productoExistente);
    }

    /**
     * Actualiza el stock de un producto
     * @param id ID del producto
     * @param cantidad Cantidad a agregar (positivo) o restar (negativo)
     * @return Producto actualizado
     */
    public Producto actualizarStock(Long id, Integer cantidad) {
        Producto producto = buscarPorId(id);
        int nuevoStock = producto.getStock() + cantidad;
        
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No hay suficiente stock disponible");
        }
        
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto por su ID
     * @param id ID del producto a eliminar
     */
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Verifica si un producto está disponible (tiene stock)
     * @param id ID del producto
     * @return true si tiene stock, false en caso contrario
     */
    public boolean estaDisponible(Long id) {
        Producto producto = buscarPorId(id);
        return producto.getStock() > 0;
    }

    /**
     * Obtiene productos por rango de precio
     * @param minPrecio Precio mínimo
     * @param maxPrecio Precio máximo
     * @return Lista de productos en el rango de precio
     */
    public List<Producto> obtenerPorRangoPrecio(BigDecimal minPrecio, BigDecimal maxPrecio) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(minPrecio, maxPrecio);
    }
}