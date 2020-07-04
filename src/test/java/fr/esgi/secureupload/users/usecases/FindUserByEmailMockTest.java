package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindUserByEmailMockTest {

    @Mock
    private UserRepository repository;

    @Before
    public void setUp (){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_ShouldReturnUserWhenFound (){

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(Mockito.mock(User.class)));

        FindUserByEmail findUserByEmail = new FindUserByEmail(repository);

        User user = findUserByEmail.execute("existing@domain.com");

        Assertions.assertNotNull(user);
        verify(repository).findByEmail(eq("existing@domain.com"));
    }

    @Test
    public void execute_ShouldThrowWhenNotFound (){

        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        FindUserByEmail findUserByEmail = new FindUserByEmail(repository);

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            findUserByEmail.execute("notexisting@domain.com");
        });

        verify(repository).findByEmail(eq("notexisting@domain.com"));
    }
}