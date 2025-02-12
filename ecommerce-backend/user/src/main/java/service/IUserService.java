package service;

import java.util.List;
import model.User;

public interface IUserService {

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param user El usuario a registrar.
     * @return El usuario registrado.
     * @throws IllegalArgumentException Si los datos del usuario no son válidos.
     * @throws UserAlreadyExistsException Si el nombre de usuario o el correo ya están en uso.
     */
    User registerUser(User user);
    
    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id   El ID del usuario a actualizar.
     * @param updatedUser Los nuevos datos del usuario.
     * @return El usuario actualizado.
     * @throws IllegalArgumentException Si el ID o los datos son inválidos.
     * @throws UserNotFoundException Si no se encuentra un usuario con el ID dado.
     */
    User updateUser(String id, User updatedUser);

    /**
     * Elimina un usuario por su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @throws IllegalArgumentException Si el ID es nulo o vacío.
     * @throws UserNotFoundException Si no se encuentra un usuario con el ID dado.
     */
    void deleteUser(String id);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username El nombre de usuario a buscar.
     * @return El usuario encontrado.
     * @throws IllegalArgumentException Si el nombre de usuario es nulo o vacío.
     * @throws UserNotFoundException Si no se encuentra un usuario con el nombre dado.
     */
    User getUserByUsername(String username);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email El correo electrónico a buscar.
     * @return El usuario encontrado.
     * @throws IllegalArgumentException Si el correo es nulo, vacío o no tiene un formato válido.
     * @throws UserNotFoundException Si no se encuentra un usuario con el correo dado.
     */
    User getUserByEmail(String email);

    /**
     * Busca un usuario por su DNI.
     *
     * @param dni El DNI a buscar.
     * @return El usuario encontrado.
     * @throws IllegalArgumentException Si el DNI es nulo o vacío.
     * @throws UserNotFoundException Si no se encuentra un usuario con el DNI dado.
     */
    User getUserByDNI(String dni);

    /**
     * Busca usuarios por su ciudad.
     *
     * @param city La ciudad a buscar.
     * @return Una lista de usuarios que viven en la ciudad especificada.
     * @throws IllegalArgumentException Si la ciudad es nula o vacía.
     */
    List<User> getUsersByCity(String city);

    /**
     * Busca usuarios por su código postal.
     *
     * @param zipCode El código postal a buscar.
     * @return Una lista de usuarios con el código postal especificado.
     * @throws IllegalArgumentException Si el código postal es nulo o vacío.
     */
    List<User> getUsersByZipCode(String zipCode);

    /**
     * Autentica a un usuario mediante su correo electrónico y contraseña.
     *
     * @param email El correo electrónico del usuario.
     * @param rawPassword La contraseña sin encriptar ingresada por el usuario.
     * @return true si las credenciales son válidas, false en caso contrario.
     * @throws IllegalArgumentException Si el correo o la contraseña son nulos o vacíos.
     * @throws UserNotFoundException Si no se encuentra un usuario con el correo dado.
     */
  
}
