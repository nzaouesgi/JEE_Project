package fr.esgi.secureupload.users.adapters.helpers;

import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserPasswordEncoderImplTest {

    UserPasswordEncoder encoder;

    public UserPasswordEncoderImplTest(@Autowired PasswordEncoder springEncoder){
        this.encoder = new UserPasswordEncoderImpl(springEncoder);
    }

    @Test
    public void encode_ShouldReturnAPasswordHash (){
        String hash = this.encoder.encode("Password");
        Assertions.assertTrue(hash.startsWith("$argon2id"));
    }

    @Test
    public void verify_EncodeAndVerifyWithGoodPassword_ShouldReturnTrue (){
        String hash = this.encoder.encode("GoodPassword");
        Assertions.assertTrue(encoder.verify("GoodPassword", hash));
    }

    @Test
    public void verify_EncodeAndVerifyWithBadPassword_ShouldReturnFalse(){
        String hash = this.encoder.encode("GoodPassword");
        Assertions.assertFalse(encoder.verify("WrongPassword", hash));
    }
}
