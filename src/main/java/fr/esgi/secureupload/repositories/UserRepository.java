package fr.esgi.secureupload.repositories;

import fr.esgi.secureupload.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    List<User> findByEmail(String email);


}
