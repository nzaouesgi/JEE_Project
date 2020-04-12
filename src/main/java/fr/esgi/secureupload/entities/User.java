package fr.esgi.secureupload.entities;

import static com.kosprov.jargon2.api.Jargon2.*;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class User {

    @Id
    @Column(name="uuid")
    private UUID uuid;

    @Column(name="email")
    @NotEmpty
    private String email;

    @Column(name="password")
    @NotEmpty
    private String password;

    @Column(name="isAdmin")
    @NotNull
    private boolean isAdmin = false;

    public User (UUID uuid, String email, String password, boolean isAdmin) {
        this.uuid = uuid;
        this.setEmail(email);
        this.setPassword(password);
        this.isAdmin = isAdmin;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        Hasher hasher = jargon2Hasher()
                .type(Type.ARGON2id)
                .memoryCost(65536)
                .timeCost(3)
                .parallelism(4)
                .saltLength(16)
                .hashLength(16);
        this.password = hasher.password(password.getBytes()).encodedHash();
    }

    public boolean verifyPassword(String password) {
        Verifier verifier = jargon2Verifier();
        return verifier.hash(this.password).password(password.getBytes()).verifyEncoded();
    }


}
