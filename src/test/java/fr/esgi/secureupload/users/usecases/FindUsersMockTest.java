package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.entities.UserOrderByField;
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

import javax.persistence.OrderBy;
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

        EntitiesPage<User> mockPage = Mockito.mock(EntitiesPage.class);
        when(repository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(mockPage);

        FindUsers findUsers = new FindUsers(this.repository);

        Assertions.assertNotNull(findUsers.execute(10, 0, UserOrderByField.EMAIL, OrderMode.ASC));

        verify(repository).findAll(eq(10),eq(0), eq(UserOrderByField.EMAIL), eq(OrderMode.ASC));
    }

    @Test
    public void execute_WhenPatternIsSupplied_ShouldCallFindAllByPattern (){

        EntitiesPage<User> mockPage = Mockito.mock(EntitiesPage.class);
        when(repository.findAllByPattern(anyInt(), anyInt(), any(), any(), anyString())).thenReturn(mockPage);

        FindUsers findUsers = new FindUsers(this.repository);

        Assertions.assertNotNull(findUsers.execute(10, 0, UserOrderByField.EMAIL, OrderMode.ASC, "1234"));

        verify(this.repository).findAllByPattern(eq(10), eq(0), eq(UserOrderByField.EMAIL), eq(OrderMode.ASC), eq("1234"));
    }

    @Test
    public void execute_WhenMaxLimitIsReached_ShouldThrow (){

        FindUsers findUsers = new FindUsers(this.repository);

        Assertions.assertThrows(UserSecurityException.class, () -> {
            findUsers.execute(FindUsers.FIND_USERS_LIMIT + 1, 0, UserOrderByField.EMAIL, OrderMode.ASC);
        });
    }
}
