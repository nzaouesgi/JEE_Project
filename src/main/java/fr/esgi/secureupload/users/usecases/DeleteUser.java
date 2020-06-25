package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.repository.UserRepository;

public class DeleteUser {

    private final UserRepository repository;

    public DeleteUser(final UserRepository repository){
        this.repository = repository;
    }

    public void execute (User user){
        this.repository.delete(user);
    }
}
