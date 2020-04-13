package fr.esgi.secureupload.repositories;

import fr.esgi.secureupload.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> { }
