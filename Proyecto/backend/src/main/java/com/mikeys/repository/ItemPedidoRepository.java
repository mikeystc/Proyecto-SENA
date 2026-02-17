package com.mikeys.repository;

import com.mikeys.model.ItemPedido;
import com.mikeys.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad ItemPedido
 * Proporciona métodos CRUD y consultas personalizadas
 */
@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    /**
     * Busca todos los items de un pedido específico
     * @param pedido Pedido al que pertenecen los items
     * @return Lista de items del pedido
     */
    List<ItemPedido> findByPedido(Pedido pedido);
}