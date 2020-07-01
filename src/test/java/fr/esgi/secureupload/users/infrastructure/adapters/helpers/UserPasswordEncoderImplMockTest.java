package fr.esgi.secureupload.users.infrastructure.adapters.helpers;

import fr.esgi.secureupload.users.domain.ports.UserPasswordEncoder;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserPasswordEncoderImplMockTest {

    @Mock
    PasswordEncoder mockSpringEncoder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
}
