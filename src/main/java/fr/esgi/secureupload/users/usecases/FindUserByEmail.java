package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;

import java.util.Objects;

public final class FindUserByEmail {

    private final UserRepository repository;

    public FindUserByEmail(final UserRepository repository){
        this.repository = repository;
    }

    public User execute (String email){
        Objects.requireNonNull(email, "Email must not be null");
        return this.repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with Email %s does not exist.", email)));
    }
}
