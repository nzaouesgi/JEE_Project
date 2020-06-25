package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.dto.ResetPasswordDTO;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.repository.UserRepository;

public class ResetUserPassword {

    private final UserRepository repository;
    private final UserPasswordEncoder encoder;

    public ResetUserPassword(final UserRepository repository, final UserPasswordEncoder encoder){
        this.repository = repository;
        this.encoder = encoder;
    }

    public User execute (User user, ResetPasswordDTO resetPasswordDto){

        if (resetPasswordDto.getCurrentPassword().equals(resetPasswordDto.getNewPassword()))
            throw new UserSecurityException("You cannot re-use the same password.");

        if (resetPasswordDto.getNewPassword().length() < User.PASSWORD_MIN_LENGTH || resetPasswordDto.getNewPassword().length() > User.PASSWORD_MAX_LENGTH)
            throw new UserSecurityException(String.format("Invalid new password. Length must be greater or equal to %d and not more than %d.",
                    User.PASSWORD_MIN_LENGTH,
                    User.PASSWORD_MAX_LENGTH));

        if (!encoder.verify(resetPasswordDto.getCurrentPassword(), user.getPassword()))
            throw new UserSecurityException("Current password is incorrect.");

        user.setPassword(encoder.encode(resetPasswordDto.getNewPassword()));
        return this.repository.save(user);
    }

}
