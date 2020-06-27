package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.TestUtils;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaEntity;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class ConfirmUserTest {

    private ConfirmUser confirmUser;
    private UserRepository userRepository;
    private TestUtils testUtils;


    public ConfirmUserTest (@Autowired ConfirmUser confirmUser, @Autowired UserJpaRepository userJpaRepository, @Autowired TestUtils testUtils){
        this.userRepository = new UserJpaRepositoryAdapter(userJpaRepository);
        this.testUtils = testUtils;
        this.confirmUser = confirmUser;
    }

    @Test
    public void execute_WhenTokenIsValid_ShouldConfirmUser () {

        User user = this.testUtils.getRandomUser(false);

        User saved = this.userRepository.save(user);

        Assertions.assertTrue(saved.isConfirmed());

        User confirmed = this.confirmUser.execute(saved, saved.getConfirmationToken());

        Assertions.assertTrue(confirmed.isConfirmed());
    }

    @Test
    public void execute_WhenTokenIsInvalid_ShouldThrowAndNotConfirm () {

        User user = this.testUtils.getRandomUser(false);
        user.setConfirmed(false);

        User saved = this.userRepository.save(user);

        Assertions.assertFalse(saved.isConfirmed());

        Assertions.assertThrows(UserSecurityException.class, () -> {
            this.confirmUser.execute(saved, "wrongtoken");
        });

        User unconfirmed = this.userRepository.findById(saved.getId()).orElse(null);

        assert unconfirmed != null;

        Assertions.assertFalse(unconfirmed.isConfirmed());
    }

}

