package com.mikeys.service;

import com.mikeys.model.Usuario;
import com.mikeys.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestionar usuarios
 * Contiene la lógica de negocio relacionada con usuarios
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario en el sistema
     * @param usuario Usuario a registrar
     * @return Usuario registrado
     * @throws IllegalArgumentException si el email ya existe
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }

        // Guardar el usuario (la contraseña debería ser encriptada en un sistema real)
        return usuarioRepository.save(usuario);
    }

    /**
     * Autentica un usuario por email y contraseña
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Usuario autenticado
     * @throws IllegalArgumentException si las credenciales son incorrectas
     */
    public Usuario autenticarUsuario(String email, String password) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmailAndPassword(email, password);
        
        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }
        
        return usuarioOptional.get();
    }

    /**
     * Busca un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado
     * @throws IllegalArgumentException si no existe el usuario
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Usuario encontrado
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene todos los usuarios
     * @return Lista de todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Actualiza un usuario existente
     * @param id ID del usuario a actualizar
     * @param usuario Datos actualizados del usuario
     * @return Usuario actualizado
     */
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario usuarioExistente = buscarPorId(id);
        
        // Verificar si el nuevo email ya existe (y no es del mismo usuario)
        if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && 
            usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        
        // Actualizar campos
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setDireccion(usuario.getDireccion());
        usuarioExistente.setTelefono(usuario.getTelefono());
        
        // Solo actualizar contraseña si se proporciona una nueva
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(usuario.getPassword());
        }
        
        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Elimina un usuario por su ID
     * @param id ID del usuario a eliminar
     */
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Verifica si existe un usuario con el email proporcionado
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}