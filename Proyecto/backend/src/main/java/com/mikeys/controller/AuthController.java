package com.mikeys.controller;

import com.mikeys.model.Usuario;
import com.mikeys.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestionar la autenticación de usuarios
 * Expone endpoints para login y registro
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para registrar un nuevo usuario
     * @param usuario Datos del usuario a registrar
     * @return ResponseEntity con el usuario registrado o mensaje de error
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            // Validar datos del usuario
            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El nombre es obligatorio"));
            }
            
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El email es obligatorio"));
            }
            
            if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(createErrorResponse("La contraseña debe tener al menos 6 caracteres"));
            }
            
            if (usuario.getDireccion() == null || usuario.getDireccion().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La dirección es obligatoria"));
            }
            
            if (usuario.getTelefono() == null || usuario.getTelefono().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El teléfono es obligatorio"));
            }

            // Registrar el usuario
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            
            // No devolver la contraseña en la respuesta
            usuarioRegistrado.setPassword(null);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("Usuario registrado exitosamente", usuarioRegistrado));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al registrar usuario: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para iniciar sesión
     * @param loginRequest Objeto con email y password
     * @return ResponseEntity con el usuario y token o mensaje de error
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validar datos de entrada
            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El email es obligatorio"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La contraseña es obligatoria"));
            }

            // Autenticar al usuario
            Usuario usuario = usuarioService.autenticarUsuario(loginRequest.getEmail(), loginRequest.getPassword());
            
            // No devolver la contraseña en la respuesta
            usuario.setPassword(null);
            
            // Crear respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Inicio de sesión exitoso");
            response.put("user", usuario);
            // En un sistema real, aquí se generaría un JWT token
            response.put("token", generateToken(usuario));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al iniciar sesión: " + e.getMessage()));
        }
    }

    /**
     * Clase interna para recibir las credenciales de login
     */
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters y Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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

    /**
     * Genera un token simple (en producción usar JWT)
     */
    private String generateToken(Usuario usuario) {
        // En un sistema real, aquí se generaría un JWT token
        // Por simplicidad, generamos un token simple
        return "token_" + usuario.getId() + "_" + System.currentTimeMillis();
    }
}