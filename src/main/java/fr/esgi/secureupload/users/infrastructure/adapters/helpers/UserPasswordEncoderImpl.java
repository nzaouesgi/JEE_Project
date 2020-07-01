package fr.esgi.secureupload.users.infrastructure.adapters.helpers;

import fr.esgi.secureupload.users.domain.ports.UserPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class UserPasswordEncoderImpl implements UserPasswordEncoder {

    PasswordEncoder encoder;

    public UserPasswordEncoderImpl(PasswordEncoder passwordEncoder){
        this.encoder = passwordEncoder;
    }

    @Override
    public String encode(CharSequence password){
        return this.encoder.encode(password);
    }

    @Override
    public boolean verify(CharSequence password, String hash) {
        return this.encoder.matches(password, hash);
    }


}
