package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.TestUtils;
import fr.esgi.secureupload.users.dto.ResetPasswordDTO;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserPropertyValidationException;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.ports.UserFieldsValidator;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResetUserPasswordMockTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserPasswordEncoder encoder;

    @Mock
    private UserFieldsValidator validator;

    @Mock
    private User user;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_WhenNewPasswordIsWeak_ShouldThrow (){

        when(validator.validatePassword("bad password")).thenReturn(false);
        when(user.getPassword()).thenReturn("some hash");
        when(encoder.verify(eq("good password"), eq("some hash"))).thenReturn(true);

        ResetUserPassword resetUserPassword = new ResetUserPassword(repository, encoder, validator);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCurrentPassword("good password");
        resetPasswordDTO.setNewPassword("bad password");

        UserPropertyValidationException e = Assertions.assertThrows(UserPropertyValidationException.class, () -> {
            resetUserPassword.execute(user, resetPasswordDTO);
        });

        Assertions.assertEquals(1, e.getErrors().size());
    }

    @Test
    public void execute_WhenCurrentPasswordIsInvalid_ShouldThrow (){

        when(user.getPassword()).thenReturn("some hash");
        when(encoder.verify(eq("wrong current password"), eq("some hash"))).thenReturn(false);

        ResetUserPassword resetUserPassword = new ResetUserPassword(repository, encoder, validator);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCurrentPassword("wrong current password");
        resetPasswordDTO.setNewPassword("new password");

        Assertions.assertThrows(UserSecurityException.class, () -> {
            resetUserPassword.execute(user, resetPasswordDTO);
        });
    }

    @Test
    public void execute_WhenRecoveryTokenIsInvalid_ShouldThrow (){


        when(user.getRecoveryToken()).thenReturn("good token");

        ResetUserPassword resetUserPassword = new ResetUserPassword(repository, encoder, validator);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setRecoveryToken("bad token");
        resetPasswordDTO.setNewPassword("NewPassword123");

        Assertions.assertThrows(UserSecurityException.class, () -> {
            resetUserPassword.execute(user, resetPasswordDTO);
        });
    }

    @Test
    public void execute_WhenDoesNotHaveRecoveryToken_ShouldThrow (){

        when(user.getRecoveryToken()).thenReturn(null);

        ResetUserPassword resetUserPassword = new ResetUserPassword(repository, encoder, validator);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setRecoveryToken("some token");
        resetPasswordDTO.setNewPassword("new password");

        Assertions.assertThrows(UserSecurityException.class, () -> {
            resetUserPassword.execute(user, resetPasswordDTO);
        });
    }

    @Test
    public void execute_WhenValidRecoveryTokenAndNewPassword_ShouldReturnUserWithNewPassword (){

        when(user.getRecoveryToken()).thenReturn("good token");

        when(validator.validatePassword("new password")).thenReturn(true);

        when(encoder.encode("new password")).thenReturn("new hash");

        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        when(user.getPassword()).thenCallRealMethod();
        doCallRealMethod().when(user).setPassword(anyString());
        user.setPassword("current hash");

        ResetUserPassword resetUserPassword = new ResetUserPassword(repository, encoder, validator);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setRecoveryToken("good token");
        resetPasswordDTO.setNewPassword("new password");

        User updated = resetUserPassword.execute(user, resetPasswordDTO);

        Assertions.assertNotNull(updated);

        Assertions.assertEquals("new hash", updated.getPassword());
    }

    @Test
    public void execute_WhenValidCurrentPasswordAndNewPassword_ShouldReturnUserWithNewPassword (){


        when(validator.validatePassword("new password")).thenReturn(true);
        when(encoder.encode("new password")).thenReturn("new hash");
        when(encoder.verify(eq("current password"), eq("current hash"))).thenReturn(true);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(user.getPassword()).thenCallRealMethod();
        doCallRealMethod().when(user).setPassword(anyString());

        user.setPassword("current hash");

        ResetUserPassword resetUserPassword = new ResetUserPassword(repository, encoder, validator);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCurrentPassword("current password");
        resetPasswordDTO.setNewPassword("new password");

        User updated = resetUserPassword.execute(user, resetPasswordDTO);

        Assertions.assertNotNull(updated);

        Assertions.assertEquals("new hash", updated.getPassword());
    }
}
