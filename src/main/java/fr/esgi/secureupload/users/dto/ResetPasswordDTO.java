package fr.esgi.secureupload.users.dto;

import fr.esgi.secureupload.users.entities.User;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
public class ResetPasswordDTO {

    @Length(min = User.PASSWORD_MIN_LENGTH, max = User.PASSWORD_MAX_LENGTH)
    private String currentPassword;

    @NotNull
    @Length(min = User.PASSWORD_MIN_LENGTH, max = User.PASSWORD_MAX_LENGTH)
    private String newPassword;

    private String recoveryToken;
}