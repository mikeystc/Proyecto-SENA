package com.mikeys.repository;

import com.mikeys.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Producto
 * Proporciona métodos CRUD y consultas personalizadas
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca productos por nombre que contenga el texto (ignorando mayúsculas)
     * @param nombre Texto a buscar en el nombre
     * @return Lista de productos que coinciden
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca productos que tengan stock mayor a 0
     * @return Lista de productos disponibles
     */
    List<Producto> findByStockGreaterThan(Integer stock);

    /**
     * Busca productos por rango de precio
     * @param minPrecio Precio mínimo
     * @param maxPrecio Precio máximo
     * @return Lista de productos en el rango de precio
     */
    List<Producto> findByPrecioBetweenOrderByPrecioAsc(java.math.BigDecimal minPrecio, 
                                                        java.math.BigDecimal maxPrecio);

    /**
     * Busca productos ordenados por fecha de creación descendente
     * @return Lista de productos ordenados
     */
    List<Producto> findAllByOrderByFechaCreacionDesc();
}