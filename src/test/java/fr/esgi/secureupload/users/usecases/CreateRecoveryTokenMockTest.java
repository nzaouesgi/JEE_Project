package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.common.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.domain.ports.UserMailSender;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateRecoveryTokenMockTest {

    @Mock
    private User user;

    @Mock
    private UserRepository repository;

    @Mock
    private RandomTokenGenerator generator;

    @Mock
    private UserMailSender sender;

    @Before
    public void CreateRecoveryTokenMockTest (){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_WithUnconfirmedUser_ShouldThrow (){

        when(user.isConfirmed()).thenReturn(false);

        CreateRecoveryToken createRecoveryToken = new CreateRecoveryToken(repository, generator, sender);

        Assertions.assertThrows(UserSecurityException.class, () -> {
            createRecoveryToken.execute(user);
        });
    }

    @Test
    public void execute_WithConfirmedUser_ShouldSetRecoveryTokenAndReturnUser (){

        when(user.isConfirmed()).thenReturn(true);

        doCallRealMethod().when(user).setRecoveryToken(anyString());
        when(user.getRecoveryToken()).thenCallRealMethod();
        when(user.getEmail()).thenReturn("mail@domain.com");

        when(generator.generate(anyInt())).thenReturn("mytoken");
        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        CreateRecoveryToken createRecoveryToken = new CreateRecoveryToken(repository, generator, sender);

        User toBeRecovered = createRecoveryToken.execute(user);

        verify(sender).sendRecoveryMail(eq("mail@domain.com"), contains("mytoken"));

        Assertions.assertNotNull(toBeRecovered);
        Assertions.assertNotNull(toBeRecovered.getRecoveryToken());
    }
}
