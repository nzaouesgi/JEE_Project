package fr.esgi.secureupload.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.esgi.secureupload.exceptions.UserExceptions;
import fr.esgi.secureupload.utils.Utils;
import lombok.*;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @NonNull
    private String email;

    /* argon2 hashed */
    @Column(name="password", nullable = false)
    @JsonIgnore
    @NonNull
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
    @Builder.Default private String confirmationToken = Utils.randomBytesToHex(64);

    /* Password recovery token sent to user's mail address, null when no reset request has been made. */
    @Column(name="recoveryToken")
    @Setter(value = AccessLevel.PACKAGE)
    @JsonIgnore
    @Builder.Default private String recoveryToken = null;

    /* Custom setter for password hashing */
    public void setPassword(String password) throws SecurityException {
        this.password = User.hashPassword(password);
    }

    public static String hashPassword (String password){
        return new Argon2PasswordEncoder().encode(password);
    }

    @Override
    public String toString (){
        return String.format("%s (%s)", this.getEmail(), this.getUuid());
    }

    public static class UserBuilder {

        /* Custom setter for password checking and hashing */
        public UserBuilder password(String password) throws UserExceptions.PropertyValidationException {
            if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH)
                throw new UserExceptions.PropertyValidationException("Password does not meet security requirements.");
            this.password = User.hashPassword(password);
            return this;
        }

        /* Custom setter for email validation */
        public UserBuilder email(String email) throws UserExceptions.PropertyValidationException {
            if (!EmailValidator.getInstance(false).isValid(email))
                throw new UserExceptions.PropertyValidationException(String.format("%s is not a valid email address.", email));
            this.email = email;
            return this;
        }
    }
}
