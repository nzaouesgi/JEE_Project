package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import fr.esgi.secureupload.users.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.dto.UserDTO;
import fr.esgi.secureupload.users.exceptions.UserMailAlreadyTakenException;
import fr.esgi.secureupload.users.repository.UserRepository;

public final class CreateUser {

    private final UserRepository repository;
    private final UserPasswordEncoder encoder;
    private final ConfirmationMailSender sender;
    private final RandomTokenGenerator generator;

    public CreateUser(final UserRepository repository, final UserPasswordEncoder encoder, final ConfirmationMailSender sender, final RandomTokenGenerator generator){
        this.repository = repository;
        this.encoder = encoder;
        this.sender = sender;
        this.generator = generator;
    }

    public User execute(final UserDTO userDto){

        if (this.repository.findByEmail(userDto.getEmail())
                .isPresent()){
            throw new UserMailAlreadyTakenException(String.format("Mail %s is already taken.", userDto.getEmail()));
        }

        String hash = this.encoder.encode(userDto.getPassword());

        User user = User.builder()
                .email(userDto.getEmail())
                .password(hash)
                .admin(false)
                .confirmed(false)
                .confirmationToken(generator.generate(32))
                .build();

        this.sender.sendConfirmationMail(user.getEmail(),
                // (this is a fake front end)
                String.format("http://secureuploadfrontend.com/confirmAccount?id=%s&confirmationToken=%s",
                        user.getEmail(),
                        user.getConfirmationToken()));

        return this.repository.save(user);
    }

}
