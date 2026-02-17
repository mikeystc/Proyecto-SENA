package com.mikeys.model;

/**
 * Enum que representa los estados posibles de un pedido
 */
public enum EstadoPedido {
    
    PENDIENTE("Pendiente", "El pedido ha sido creado y está esperando procesamiento"),
    
    PROCESANDO("Procesando", "El pedido está siendo preparado"),
    
    ENVIADO("Enviado", "El pedido ha sido enviado al cliente"),
    
    ENTREGADO("Entregado", "El pedido ha sido entregado al cliente"),
    
    CANCELADO("Cancelado", "El pedido ha sido cancelado");

    private final String nombre;
    private final String descripcion;

    EstadoPedido(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}