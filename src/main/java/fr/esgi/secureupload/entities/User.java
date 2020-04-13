package fr.esgi.secureupload.entities;

import fr.esgi.secureupload.utils.Crypto;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;


@Getter
@Builder
@Entity(name="User")
@Table(name="users")
public class User extends BaseEntity {

    @Column(name="email", unique = true)
    @NotEmpty
    @NonNull
    private String email;

    @Column(name="password")
    @NotEmpty
    @NonNull
    private String password;

    @Column(name="isAdmin", nullable = false)
    @Setter
    @Builder.Default private boolean isAdmin = false;

    @Column(name="isConfirmed", nullable = false)
    @Setter
    @Builder.Default private boolean isConfirmed = false;

    @Column(name="confirmationToken", nullable = false)
    @NotEmpty
    @Builder.Default private String confirmationToken = Crypto.randomString(64);

    @Column(name="recoveryToken")
    @Setter
    @Builder.Default private String recoveryToken = null;

    public boolean verifyPassword(String clearPassword) {
        return Crypto.verifyPassword(this.password, clearPassword);
    }

    public void setPassword(String clearPassword){
        if (!this.verifyPassword(clearPassword)){
            throw new SecurityException(String.format("Password changed failed for user %s (wrong password).", this.toString()));
        }
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

}
