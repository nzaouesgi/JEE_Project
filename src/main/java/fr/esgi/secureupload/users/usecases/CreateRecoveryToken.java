package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.common.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.domain.ports.UserMailSender;
import fr.esgi.secureupload.users.domain.repository.UserRepository;

import java.util.Objects;

public class CreateRecoveryToken {

    private final UserRepository repository;
    private final RandomTokenGenerator generator;
    private final UserMailSender sender;

    public CreateRecoveryToken (final UserRepository repository, final RandomTokenGenerator generator, final UserMailSender sender){
        this.repository = repository;
        this.generator = generator;
        this.sender = sender;
    }

    public User execute (User user){

        Objects.requireNonNull(user, "User must not be null");

        if (!user.isConfirmed()){
            throw new UserSecurityException("This account must be confirmed.");
        }

        user.setRecoveryToken(this.generator.generate(32));

        sender.sendRecoveryMail(user.getEmail(),

                // (this is a fake front end)
                String.format("http://secureuploadfrontend.com/recover?id=%s&recoveryToken=%s",
                        user.getId(),
                        user.getRecoveryToken()));

        return this.repository.save(user);
    }
}
