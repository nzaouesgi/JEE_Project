package fr.esgi.secureupload.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.esgi.secureupload.utils.Crypto;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity(name="User")
@Table(name="users")
public class User extends BaseEntity {

    public static final String [] PRIVATE_FIELDS = new String [] { "password", "recoveryToken", "confirmationToken" };

    @Column(name="email", unique = true, nullable = false)
    @NotBlank
    @NonNull
    private String email;

    @Column(name="password", nullable = false)
    @NotBlank
    @NonNull
    @JsonIgnore
    private String password;

    @Column(name="isAdmin", nullable = false)
    @Setter(value = AccessLevel.PACKAGE)
    @Builder.Default private boolean isAdmin = false;

    @Column(name="isConfirmed", nullable = false)
    @Setter
    @Builder.Default private boolean isConfirmed = false;

    @Column(name="confirmationToken", nullable = false)
    @NotEmpty
    @JsonIgnore
    @Builder.Default private String confirmationToken = Crypto.randomString(64);

    @Column(name="recoveryToken")
    @Setter(value = AccessLevel.PACKAGE)
    @JsonIgnore
    @Builder.Default private String recoveryToken = null;

    public boolean verifyPassword(String clearPassword) {
        return Crypto.verifyPassword(this.password, clearPassword);
    }

    public void setPassword(String clearPassword) throws SecurityException{
        this.password = Crypto.passwordHash(clearPassword);
    }

    @Override
    public String toString (){
        return String.format("%s (%s)", this.getEmail(), this.getUuid());
    }

    public static class UserBuilder {
        public UserBuilder password(String password) {
            this.password = Crypto.passwordHash(password);
            return this;
        }
    }

    @Data
    public static class CreateDto {
        private String email;
        private String password;
    }

    @Data
    public static class ResetPasswordDto {
        private String currentPassword;
        private String newPassword;
    }

    public static class NotFoundException extends Exception {
        public NotFoundException (String message){
            super(message);
        }
    }
    public static class PropertyNotFoundException extends Exception {
        public PropertyNotFoundException (String message){
            super(message);
        }
    }
    public static class MailAlreadyTakenException extends Exception {
        public MailAlreadyTakenException (String message){
            super(message);
        }
    }

    public static class SecurityException extends Exception {
        public SecurityException (String message){
            super(message);
        }
    }

}
