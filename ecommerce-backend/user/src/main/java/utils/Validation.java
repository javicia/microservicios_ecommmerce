package utils;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import exception.UserAlreadyExistsException;
import model.User;
import repository.UserRepository;

/**
 * Clase utilitaria para validaciones relacionadas con el usuario.
 */
@Component
public class Validation {
	
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

	// Patrón para validar emails
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Patrón para validar DNI (ejemplo para DNI español: 8 dígitos seguidos de una letra mayúscula)
    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}[A-Z]$");

    @Autowired
    public Validation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Método para validar el formato del correo electrónico.
     *
     * @param email El correo electrónico a validar.
     * @return true si el correo electrónico es válido, false en caso contrario.
     */
    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Método para validar el formato del DNI.
     *
     * @param dni El DNI a validar.
     * @return true si el DNI es válido, false en caso contrario.
     */
    public boolean isValidDni(String dni) {
        if (dni == null) return false;
        return DNI_PATTERN.matcher(dni).matches();
    }
    
    /**
     * Valida los datos necesarios para registrar un nuevo usuario.
     *
     * @param user El usuario a registrar.
     */
    public void validateUserForRegistration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }

        if (!isValidDni(user.getDni())) {
            throw new IllegalArgumentException("El DNI no es válido.");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya está en uso.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está en uso.");
        }
        if (userRepository.existsByDni(user.getDni())) {
            throw new UserAlreadyExistsException("El DNI ya está en uso.");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
    }
    
    /**
     * Actualiza los datos del usuario existente con los nuevos datos proporcionados.
     *
     * @param existingUser El usuario existente en la base de datos.
     * @param updatedUser  Los nuevos datos para actualizar.
     */
    public void updateExistingUser(User existingUser, User updatedUser) {
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().trim().isEmpty()) {
            if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                userRepository.existsByUsername(updatedUser.getUsername())) {
                throw new UserAlreadyExistsException("El nombre de usuario ya está en uso.");
            }
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getSurname() != null) {
            existingUser.setSurname(updatedUser.getSurname());
        }
        if (updatedUser.getDni() != null && !updatedUser.getDni().trim().isEmpty()) {
            if (!isValidDni(updatedUser.getDni())) {
                throw new IllegalArgumentException("El formato del DNI no es válido.");
            }
            if (!existingUser.getDni().equals(updatedUser.getDni()) &&
                userRepository.existsByDni(updatedUser.getDni())) {
                throw new UserAlreadyExistsException("El DNI ya está en uso.");
            }
            existingUser.setDni(updatedUser.getDni());
        }
        if (updatedUser.getEmail() != null) {
            if (!isValidEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("El formato del correo electrónico no es válido.");
            }
            if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new UserAlreadyExistsException("El correo electrónico ya está en uso.");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getRole() != null && !updatedUser.getRole().trim().isEmpty()) {
            existingUser.setRole(updatedUser.getRole());
        }
        // Actualiza otros campos según sea necesario
    }

}
