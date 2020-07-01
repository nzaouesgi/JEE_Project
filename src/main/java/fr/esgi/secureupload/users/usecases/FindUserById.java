package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;

import java.util.Objects;

public final class FindUserById {

    private final UserRepository repository;

    public FindUserById(final UserRepository repository){
        this.repository = repository;
    }

    public User execute (String id){
        Objects.requireNonNull(id, "id must not be null");
        return this.repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s does not exist.", id)));
    }
}
