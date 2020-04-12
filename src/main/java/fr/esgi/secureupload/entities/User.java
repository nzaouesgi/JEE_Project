package fr.esgi.secureupload.entities;

import lombok.Data;

import static com.kosprov.jargon2.api.Jargon2.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity(name="User")
@Table(name="users")
public class User extends BaseEntity {

    @Column(name="email", unique = true)
    @NotEmpty
    private String email;

    @Column(name="password")
    @NotEmpty
    private String password;

    @Column(name="isAdmin")
    @NotNull
    private boolean isAdmin = false;

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


    @Override
    public String toString (){
        return String.format("%s (%s)", this.getEmail(), this.getUuid());
    }
}
