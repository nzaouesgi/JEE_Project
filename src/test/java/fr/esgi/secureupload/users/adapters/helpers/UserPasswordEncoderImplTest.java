package fr.esgi.secureupload.users.adapters.helpers;

import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserPasswordEncoderImplTest {

    UserPasswordEncoder encoder;

    @Mock
    PasswordEncoder mockSpringEncoder;

    public UserPasswordEncoderImplTest(@Autowired PasswordEncoder springEncoder){
        this.encoder = new UserPasswordEncoderImpl(springEncoder);
    }

    @Test
    public void encode_ShouldCallEncodeInSpringEncoder (){

        UserPasswordEncoder encoderWithMockSpringEncoder = new UserPasswordEncoderImpl(mockSpringEncoder);

        String password = "password";

        encoderWithMockSpringEncoder.encode(password);
        verify(mockSpringEncoder).encode(password);
    }

    @Test
    public void verify_ShouldCallEncodeInSpringEncoder (){

        UserPasswordEncoder encoderWithMockSpringEncoder = new UserPasswordEncoderImpl(mockSpringEncoder);

        String password = "password";
        String hash = "$argon2id$v=19$m=16,t=2,p=1$ZlJGMXBBejNwRmt6bFZuRQ$oYWtKMFNUBV30OHw7YjcdQ";

        encoderWithMockSpringEncoder.verify(password, hash);
        verify(mockSpringEncoder).matches(password, hash);
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
