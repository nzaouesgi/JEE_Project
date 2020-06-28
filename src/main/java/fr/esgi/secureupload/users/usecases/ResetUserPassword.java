package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.dto.ResetPasswordDTO;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserPropertyValidationException;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.ports.UserFieldsValidator;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.repository.UserRepository;

import java.util.List;
import java.util.Objects;

public class ResetUserPassword {

    private final UserRepository repository;
    private final UserPasswordEncoder encoder;
    private final UserFieldsValidator validator;

    public ResetUserPassword(final UserRepository repository, final UserPasswordEncoder encoder, final UserFieldsValidator validator){
        this.repository = repository;
        this.encoder = encoder;
        this.validator = validator;
    }

    public User execute (User user, ResetPasswordDTO resetPasswordDto){

        Objects.requireNonNull(user, "User must not be null.");
        Objects.requireNonNull(resetPasswordDto, "Reset password DTO must not be null.");
        Objects.requireNonNull(resetPasswordDto.getNewPassword(), "New password must not be null.");

        if (Objects.isNull(resetPasswordDto.getCurrentPassword()) && Objects.isNull(resetPasswordDto.getRecoveryToken())){
            throw new NullPointerException("Either current password or recovery token must not be null.");
        }

        // Password reset with recovery token.
        if (!Objects.isNull(resetPasswordDto.getRecoveryToken())){

            if (Objects.isNull(user.getRecoveryToken()))
                throw new UserSecurityException("No token has been generated for this user.");

            if (!resetPasswordDto.getRecoveryToken().equals(user.getRecoveryToken()))
                throw new UserSecurityException("Token is invalid.");
        }

        // Regular password reset.
        else if (!Objects.isNull(resetPasswordDto.getCurrentPassword())) {

            if (!encoder.verify(resetPasswordDto.getCurrentPassword(), user.getPassword()))
                throw new UserSecurityException("Current password is incorrect.");

            if (resetPasswordDto.getCurrentPassword().equals(resetPasswordDto.getNewPassword()))
                throw new UserSecurityException("You cannot re-use the same password.");
        }

        if (!validator.validatePassword(resetPasswordDto.getNewPassword()))
            throw new UserPropertyValidationException(List.of(String.format("Invalid new password. Length must be greater or equal to %d and not more than %d.",
                    User.PASSWORD_MIN_LENGTH,
                    User.PASSWORD_MAX_LENGTH)));

        user.setPassword(encoder.encode(resetPasswordDto.getNewPassword()));

        return this.repository.save(user);
    }

}
