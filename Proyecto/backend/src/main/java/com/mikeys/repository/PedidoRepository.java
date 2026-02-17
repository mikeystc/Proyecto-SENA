package com.mikeys.repository;

import com.mikeys.model.Pedido;
import com.mikeys.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Pedido
 * Proporciona métodos CRUD y consultas personalizadas
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca todos los pedidos de un usuario específico
     * @param usuario Usuario propietario de los pedidos
     * @return Lista de pedidos del usuario
     */
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);

    /**
     * Busca pedidos por estado
     * @param estado Estado del pedido
     * @return Lista de pedidos con el estado especificado
     */
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(com.mikeys.model.EstadoPedido estado);

    /**
     * Cuenta los pedidos por estado
     * @param estado Estado a contar
     * @return Número de pedidos con el estado especificado
     */
    long countByEstado(com.mikeys.model.EstadoPedido estado);
}