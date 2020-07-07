package fr.esgi.secureupload.users.infrastructure.adapters;

import fr.esgi.secureupload.common.infrastructure.adapters.BaseJPAEntity;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaEntity;

import javax.persistence.*;
import java.util.List;

@Entity(name="User")
@Table(name="users")
public class UserJpaEntity extends BaseJPAEntity {

    public UserJpaEntity(){}

    @Column(name="email", unique = true, nullable = false)
    private String email;

    /* argon2 hashed */
    @Column(name="password", nullable = false)
    private String password;

    @Column(name="is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name="is_confirmed", nullable = false)
    private boolean isConfirmed;

    @Column(name="confirmation_token", nullable = false)
    private String confirmationToken;

    @Column(name="recovery_token")
    private String recoveryToken;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "owner")
    private List<FileJpaEntity> files;

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

    public List<FileJpaEntity> getFiles() {
        return files;
    }

    public void setFiles(List<FileJpaEntity> files) {
        this.files = files;
    }
}
