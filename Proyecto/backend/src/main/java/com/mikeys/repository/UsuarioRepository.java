package com.mikeys.repository;

import com.mikeys.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario
 * Proporciona métodos CRUD y consultas personalizadas
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email proporcionado
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por email y contraseña
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmailAndPassword(String email, String password);
}