package fr.esgi.secureupload.security.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

@SpringBootTest
public class JWTProviderTest {

    private final JWTProvider provider;

    public JWTProviderTest (@Autowired JWTProvider provider){
        this.provider = provider;
    }

    @Test
    public void createTokenTest (){

        UsernamePasswordAuthenticationToken auth = Mockito.mock(UsernamePasswordAuthenticationToken.class);

        when(auth.getAuthorities()).thenCallRealMethod();

        this.provider.createToken(auth);
    }
}
