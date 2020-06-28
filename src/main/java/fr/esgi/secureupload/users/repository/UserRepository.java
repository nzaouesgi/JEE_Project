package fr.esgi.secureupload.users.repository;

import fr.esgi.secureupload.users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    Page<User> findAllByPattern(String pattern, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    User save (User user);

    void deleteById (String id);
}
