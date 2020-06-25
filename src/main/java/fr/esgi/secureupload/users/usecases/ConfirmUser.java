package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.repository.UserRepository;

public final class ConfirmUser {

    private final UserRepository repository;

    public ConfirmUser(final UserRepository repository){
        this.repository = repository;
    }

    public User execute (final User user, final String confirmationToken){

        if (!user.getConfirmationToken().equals(confirmationToken)){
            throw new UserSecurityException("An invalid confirmation token was supplied.");
        }

        user.setConfirmed(true);
        return this.repository.save(user);
    }
}
