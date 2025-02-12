package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import model.User;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByDni(String dni);
    boolean existsByDni(String dni);
    List<User> findByCity(String city);
    List<User> findByZipCode(String zipCode);
}
