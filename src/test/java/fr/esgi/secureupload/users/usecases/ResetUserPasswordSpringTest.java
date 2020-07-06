package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.utils.TestUtils;
import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.infrastructure.dto.ResetPasswordDTO;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserPropertyValidationException;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ResetUserPasswordSpringTest extends SpringTestWithUsers {

    private final ResetUserPassword resetUserPassword;

    private final UserRepository repository;

    public ResetUserPasswordSpringTest (@Autowired ResetUserPassword resetUserPassword, @Autowired UserJpaRepository jpaRepository){
        this.resetUserPassword = resetUserPassword;
        this.repository = new UserJpaRepositoryAdapter(jpaRepository);
    }

    @Test
    public void execute_WhenNewPasswordIsWeak_ShouldThrow (){

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCurrentPassword(TestUtils.DEFAULT_PASSWORD);
        resetPasswordDTO.setNewPassword("bad");

        UserPropertyValidationException e = Assertions.assertThrows(UserPropertyValidationException.class, () -> {
            this.resetUserPassword.execute(users.get(0), resetPasswordDTO);
        });

        Assertions.assertEquals(1, e.getErrors().size());
    }

    @Test
    public void execute_WhenCurrentPasswordIsInvalid_ShouldThrow (){

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCurrentPassword("Bad password");
        resetPasswordDTO.setNewPassword("bad");

        Assertions.assertThrows(UserSecurityException.class, () -> {
            this.resetUserPassword.execute(users.get(0), resetPasswordDTO);
        });
    }

    @Test
    public void execute_WhenRecoveryTokenIsInvalid_ShouldThrow (){

        users.get(0).setRecoveryToken("good");
        users.set(0, this.repository.save(users.get(0)));

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setRecoveryToken("bad");
        resetPasswordDTO.setNewPassword("NewPassword123");

        Assertions.assertThrows(UserSecurityException.class, () -> {
            this.resetUserPassword.execute(users.get(0), resetPasswordDTO);
        });
    }

    @Test
    public void execute_WhenDoesNotHaveRecoveryToken_ShouldThrow (){

        users.get(0).setRecoveryToken(null);
        users.set(0, this.repository.save(users.get(0)));

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setRecoveryToken("sometoken");
        resetPasswordDTO.setNewPassword("NewPassword123");

        Assertions.assertThrows(UserSecurityException.class, () -> {
            this.resetUserPassword.execute(users.get(0), resetPasswordDTO);
        });
    }

    @Test
    public void execute_WhenValidRecoveryTokenAndNewPassword_ShouldReturnUserWithNewPassword (){

        users.get(0).setRecoveryToken("good");
        users.set(0, this.repository.save(users.get(0)));

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setRecoveryToken("good");
        resetPasswordDTO.setNewPassword("NewPassword123");

        User updated = this.resetUserPassword.execute(users.get(0), resetPasswordDTO);

        Assertions.assertNotNull(updated);

        Assertions.assertNotEquals(TestUtils.DEFAULT_PASSWORD, updated.getPassword());
    }

    @Test
    public void execute_WhenValidCurrentPasswordAndNewPassword_ShouldReturnUserWithNewPassword (){

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCurrentPassword(TestUtils.DEFAULT_PASSWORD);
        resetPasswordDTO.setNewPassword("SomeNewPassword123");

        User updated = this.resetUserPassword.execute(users.get(0), resetPasswordDTO);

        Assertions.assertNotNull(updated);

        Assertions.assertNotEquals(TestUtils.DEFAULT_PASSWORD, updated.getPassword());
    }
}
