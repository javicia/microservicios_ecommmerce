package controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.User;
import service.IUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Usuarios", description = "CRUD de usuarios")
@RestController
@CrossOrigin(origins = "*") // Permite recibir peticiones desde cualquier origen
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;

    @Autowired
    private RestTemplate restTemplate;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * Llamada de prueba a través del balanceador de carga.
     */
    @Operation(summary = "Llamada de prueba a través del balanceador de carga")
    @GetMapping("/details")
    public ResponseEntity<String> getUserDetails() {
        log.info("Iniciando llamada de prueba al balanceador de carga.");
        String response = restTemplate.getForObject("http://user-ecommerce/api/users/details", String.class);
        log.info("Respuesta recibida del balanceador de carga: {}", response);
        return ResponseEntity.ok(response);
    }
  
    /**
     * Registra un nuevo usuario.
     */
    @Operation(summary = "Registra un nuevo usuario")
    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        log.info("Solicitud de registro de usuario recibida. Datos: {}", user);
        try {
            User registeredUser = userService.registerUser(user);
            log.info("Usuario registrado con éxito: {}", registeredUser);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            log.error("El usuario ya existe: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            log.error("Error en los datos proporcionados: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inesperado al registrar usuario: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualiza un usuario existente.
     */
    @Operation(summary = "Actualiza un usuario existente")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody @Valid User user) {
        log.info("Solicitud de actualización para el usuario con ID {}: {}", id, user);
        try {
            User updatedUser = userService.updateUser(id, user);
            log.info("Usuario actualizado con éxito: {}", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("Error en los datos proporcionados: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inesperado al actualizar usuario: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina un usuario existente.
     */
    @Operation(summary = "Elimina un usuario existente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Solicitud de eliminación para el usuario con ID {}", id);
        try {
            userService.deleteUser(id);
            log.info("Usuario eliminado con éxito: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("Error en los datos proporcionados: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inesperado al eliminar usuario: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Busca un usuario por su nombre de usuario.
     */
    @Operation(summary = "Busca un usuario por su nombre de usuario")
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        log.info("Buscando usuario por nombre de usuario: {}", username);
        try {
            User user = userService.getUserByUsername(username);
            log.info("Usuario encontrado: {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Busca un usuario por su correo electrónico.
     */
    @Operation(summary = "Busca un usuario por su correo electrónico")
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.info("Buscando usuario por correo electrónico: {}", email);
        try {
            User user = userService.getUserByEmail(email);
            log.info("Usuario encontrado: {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Busca un usuario por su DNI.
     */
    @Operation(summary = "Busca un usuario por su DNI")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<User> getUserByDNI(@PathVariable String dni) {
        log.info("Buscando usuario por DNI: {}", dni);
        try {
            User user = userService.getUserByDNI(dni);
            log.info("Usuario encontrado: {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Busca usuarios por ciudad.
     */
    @Operation(summary = "Busca usuarios por ciudad")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<User>> getUsersByCity(@PathVariable String city) {
        log.info("Buscando usuarios por ciudad: {}", city);
        try {
            List<User> users = userService.getUsersByCity(city);
            log.info("Usuarios encontrados: {}", users);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error en los datos proporcionados: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Busca usuarios por código postal.
     */
    @Operation(summary = "Busca usuarios por código postal")
    @GetMapping("/zipcode/{zipCode}")
    public ResponseEntity<List<User>> getUsersByZipCode(@PathVariable String zipCode) {
        log.info("Buscando usuarios por código postal: {}", zipCode);
        try {
            List<User> users = userService.getUsersByZipCode(zipCode);
            log.info("Usuarios encontrados: {}", users);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error en los datos proporcionados: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
