package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.exceptions.UserPropertyValidationException;
import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import fr.esgi.secureupload.users.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.ports.UserFieldsValidator;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.dto.UserDTO;
import fr.esgi.secureupload.users.exceptions.UserMailAlreadyTakenException;
import fr.esgi.secureupload.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public final class CreateUser {

    private final UserRepository repository;
    private final UserPasswordEncoder encoder;
    private final ConfirmationMailSender sender;
    private final RandomTokenGenerator generator;
    private final UserFieldsValidator validator;

    public CreateUser(final UserRepository repository, final UserPasswordEncoder encoder, final ConfirmationMailSender sender, final RandomTokenGenerator generator, final UserFieldsValidator validator){
        this.repository = repository;
        this.encoder = encoder;
        this.sender = sender;
        this.generator = generator;
        this.validator = validator;
    }

    public User execute(final UserDTO userDto){

        userDto.setEmail(userDto.getEmail().trim());

        List<String> errors = new ArrayList<>();

        if (!this.validator.validateMail(userDto.getEmail()))
            errors.add(String.format("%s is not a valid mail address.", userDto.getEmail()));

        if (!this.validator.validatePassword(userDto.getPassword()))
            errors.add("Password does not meet security requirements.");

        if (errors.size() > 0)
            throw new UserPropertyValidationException(errors);

        if (this.repository.findByEmail(userDto.getEmail()).isPresent())
            throw new UserMailAlreadyTakenException(String.format("Mail %s is already taken.", userDto.getEmail()));

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
