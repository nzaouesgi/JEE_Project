package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.domain.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Objects;

public final class DeleteUser {

    private final UserRepository repository;

    public DeleteUser(final UserRepository repository){
        this.repository = repository;
    }

    public void execute (String id){
        Objects.requireNonNull(id, "id must not be null");
        try {
            this.repository.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new UserNotFoundException(String.format("User with ID %s does not exist.", id));
        }
    }
}
