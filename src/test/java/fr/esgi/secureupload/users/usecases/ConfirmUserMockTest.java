package fr.esgi.secureupload.users.usecases;


import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfirmUserMockTest {

    @Test
    public void execute_WhenTokenIsValid_ShouldFindUserInRepoAndSaveItAfterConfirmed (){

        User mockUser = Mockito.mock(User.class);
        when(mockUser.getConfirmationToken()).thenReturn("goodtoken");

        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        when(userRepositoryMock.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        ConfirmUser confirmUser = new ConfirmUser(userRepositoryMock);

        Assertions.assertDoesNotThrow(() -> {
            confirmUser.execute(mockUser, "goodtoken");
        });

        verify(mockUser).setConfirmed(true);
        verify(userRepositoryMock).save(any(User.class));
    }

    @Test
    public void execute_WhenTokenIsInvalid_ShouldNotConfirmUser (){

        User mockUser = Mockito.mock(User.class);
        when(mockUser.getConfirmationToken()).thenReturn("goodtoken");

        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        ConfirmUser confirmUser = new ConfirmUser(userRepositoryMock);

        Assertions.assertThrows(UserSecurityException.class, () -> {
            confirmUser.execute(mockUser, "badtoken");
        });

        verify(mockUser, never()).setConfirmed(anyBoolean());
        verify(userRepositoryMock, never()).save(any(User.class));
    }
}
