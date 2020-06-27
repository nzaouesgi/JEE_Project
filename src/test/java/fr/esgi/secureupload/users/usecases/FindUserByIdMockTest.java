package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindUserByIdMockTest {

    @Mock
    private UserRepository repository;

    @Before
    public void setUp (){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_ShouldCallFindById (){

        when(repository.findById(anyString())).thenReturn(Optional.of(Mockito.mock(User.class)));

        FindUserById findUserById = new FindUserById(repository);

        User user = findUserById.execute("existing ID");

        Assertions.assertNotNull(user);

        verify(repository).findById(eq("existing ID"));
    }

    @Test
    public void execute_ShouldThrowWhenNotFound (){

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        FindUserById findUserById = new FindUserById(repository);

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            findUserById.execute("does not exist ID");
        });

        verify(repository).findById(eq("does not exist ID"));
    }
}
