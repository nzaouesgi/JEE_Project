package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Random;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindUsersMockTest {

    @Mock
    private UserRepository repository;

    @Before
    public void setUp (){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_WhenPatternNotSupplied_ShouldCallFindAll (){

        Page<User> mockPage = Mockito.mock(Page.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(mockPage);

        FindUsers findUsers = new FindUsers(this.repository);

        Assertions.assertNotNull(findUsers.execute(10, 0, "email", "asc"));

        verify(repository).findAll(eq(PageRequest.of(0, 10, Sort.by("email"))));
    }

    @Test
    public void execute_WhenPatternIsSupplied_ShouldCallFindAllByPattern (){

        Page<User> mockPage = (Page<User>) Mockito.mock(Page.class);
        when(this.repository.findAllByPattern(anyString(), any(Pageable.class))).thenReturn(mockPage);

        FindUsers findUsers = new FindUsers(this.repository);

        Assertions.assertNotNull(findUsers.execute(10, 0, "email", "asc", "1234"));

        verify(this.repository).findAllByPattern(eq("1234"), eq(PageRequest.of(0, 10, Sort.by("email"))));
    }

    @Test
    public void execute_WhenMaxLimitIsReached_ShouldThrow (){

        FindUsers findUsers = new FindUsers(this.repository);

        Assertions.assertThrows(UserSecurityException.class, () -> {
            findUsers.execute(FindUsers.FIND_USERS_LIMIT + 1, 0, "email", "asc");
        });
    }

    @Test
    public void execute_WhenPrivateFieldIsSuppliedInOrderBy_ShouldThrow (){
        FindUsers findUsers = new FindUsers(this.repository);

        Assertions.assertThrows(UserSecurityException.class, () -> {
            findUsers.execute(FindUsers.FIND_USERS_LIMIT + 1, 0, User.PRIVATE_FIELDS[new Random().nextInt(User.PRIVATE_FIELDS.length)], "asc");
        });
    }
}
