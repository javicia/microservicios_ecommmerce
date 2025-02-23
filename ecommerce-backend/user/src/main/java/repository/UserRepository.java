package repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import model.User;

public interface UserRepository extends MongoRepository<User, String>{

	/**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email El correo electrónico a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario con el nombre de usuario especificado.
     *
     * @param username El nombre de usuario a verificar.
     * @return true si el nombre de usuario ya existe, false en caso contrario.
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si ya existe un usuario con el correo electrónico especificado.
     *
     * @param email El correo electrónico a verificar.
     * @return true si el correo electrónico ya existe, false en caso contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por su DNI.
     *
     * @param DNI El DNI a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<User> findByDni(String dni);
    
    /**
     * Verifica si ya existe un usuario con el DNI especificado.
     *
     * @param dni El DNI a verificar.
     * @return true si el DNI ya existe, false en caso contrario.
     */
    boolean existsByDni(String dni);

    /**
     * Busca usuarios por la ciudad.
     *
     * @param city La ciudad a buscar.
     * @return Una lista de usuarios que viven en la ciudad especificada.
     */
    List<User> findByCity(String city);

    /**
     * Busca usuarios por código postal.
     *
     * @param zipCode El código postal a buscar.
     * @return Una lista de usuarios que tienen el código postal especificado.
     */
    List<User> findByZipCode(String zipCode);

    /**
     * Busca un usuario por su ID.
     *
     * @param id El ID del usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<User> findById(String id);
}
