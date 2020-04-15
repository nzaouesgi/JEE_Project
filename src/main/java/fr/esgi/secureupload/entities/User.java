package fr.esgi.secureupload.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.esgi.secureupload.utils.Utils;
import lombok.*;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity(name="User")
@Table(name="users")
public class User extends BaseEntity {

    public static final String [] PRIVATE_FIELDS = new String [] { "password", "recoveryToken", "confirmationToken" };
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 100;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    /* argon2 hashed */
    @Column(name="password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name="isAdmin", nullable = false)
    @Setter(value = AccessLevel.PACKAGE)
    @Builder.Default private boolean isAdmin = false;

    /* If the user's mail address has been confirmed */
    @Column(name="isConfirmed", nullable = false)
    @Setter
    @Builder.Default private boolean isConfirmed = false;

    /* Random token sent to user's mail address to confirm it. */
    @Column(name="confirmationToken", nullable = false)
    @JsonIgnore
    @Builder.Default private String confirmationToken = Utils.randomString(64);

    /* Password recovery token sent to user's mail address, null when no reset request has been made. */
    @Column(name="recoveryToken")
    @Setter(value = AccessLevel.PACKAGE)
    @JsonIgnore
    @Builder.Default private String recoveryToken = null;

    /* Custom setter for password hashing */
    public void setPassword(String clearPassword) throws SecurityException{
        this.password = new Argon2PasswordEncoder().encode(clearPassword);
    }

    @Override
    public String toString (){
        return String.format("%s (%s)", this.getEmail(), this.getUuid());
    }

    public static class UserBuilder {

        /* Custom setter for password hashing */
        public UserBuilder password(String password) {
            this.password = new Argon2PasswordEncoder().encode(password);
            return this;
        }

        /* Custom setter for email validation */
        public UserBuilder email(String email) throws PropertyValidationException {
            if (!EmailValidator.getInstance(false).isValid(email))
                throw new PropertyValidationException(String.format("%s is not a valid email address.", email));
            this.email = email;
            return this;
        }
    }

    /* Object to be received at user creation endpoint */
    @Data
    public static class CreateDto {
        @NotBlank
        private String email;

        @Length(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
        private String password;
    }

    /* Object to be received at password reset endpoint */
    @Data
    public static class ResetPasswordDto {

        @Length(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
        private String currentPassword;

        @Length(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
        private String newPassword;
    }

    @Data
    public static class LoginDto {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    /* User property validation failure */
    public static class PropertyValidationException extends Exception {
        public PropertyValidationException (String message){
            super(message);
        }
    }

    /* Non existing user */
    public static class NotFoundException extends Exception {
        public NotFoundException (String message){
            super(message);
        }
    }

    /* To be used when bad orderBy parameter is supplied */
    public static class PropertyNotFoundException extends Exception {
        public PropertyNotFoundException (String message){
            super(message);
        }
    }

    /* Trying to create a account with existing mail */
    public static class MailAlreadyTakenException extends Exception {
        public MailAlreadyTakenException (String message){
            super(message);
        }
    }

    /* Wrong password, unsafe password, private field related issues.  */
    public static class SecurityException extends Exception {
        public SecurityException (String message){
            super(message);
        }
    }

}
