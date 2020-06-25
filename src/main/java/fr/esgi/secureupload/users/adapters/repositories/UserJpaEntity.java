package fr.esgi.secureupload.users.adapters.repositories;

import fr.esgi.secureupload.common.repository.BaseJPAEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="User")
@Table(name="users")
public class UserJpaEntity extends BaseJPAEntity {

    public UserJpaEntity(){}

    @Column(name="email", unique = true, nullable = false)
    private String email;

    /* argon2 hashed */
    @Column(name="password", nullable = false)
    private String password;

    @Column(name="isAdmin", nullable = false)
    private boolean isAdmin;

    @Column(name="isConfirmed", nullable = false)
    private boolean isConfirmed;

    @Column(name="confirmationToken", nullable = false)
    private String confirmationToken;

    @Column(name="recoveryToken")
    private String recoveryToken;

    public String getEmail() { return this.email; }

    public String getPassword() {
        return this.password;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public boolean isConfirmed() {
        return this.isConfirmed;
    }

    public String getConfirmationToken() {
        return this.confirmationToken;
    }

    public String getRecoveryToken() {
        return this.recoveryToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }
}
