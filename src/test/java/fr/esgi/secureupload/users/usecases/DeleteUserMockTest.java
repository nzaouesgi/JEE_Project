package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.domain.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteUserMockTest {

    @Mock
    private UserRepository repository;

    @Before
    public void setUp (){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_ShouldCallDeleteById (){
        DeleteUser deleteUser = new DeleteUser(repository);
        deleteUser.execute("someid");
        verify(repository).deleteById(eq("someid"));
    }

    @Test
    public void execute_WhenInvalidId_ShouldThrow (){
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(anyString());
        DeleteUser deleteUser = new DeleteUser(repository);
        Assertions.assertThrows(UserNotFoundException.class, () -> {
           deleteUser.execute("notexisting");
        });
    }

}
