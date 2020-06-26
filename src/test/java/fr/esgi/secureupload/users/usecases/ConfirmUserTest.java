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

    // REAL TESTS

    @Test
    public void execute_WhenTokenIsValid_ShouldConfirmUser () {

        String confirmationToken = "mytoken";

        User user = User.builder()
                .email(this.testUtils.getRandomMail())
                .password("$argon2id$v=19$m=16,t=2,p=1$ZlJGMXBBejNwRmt6bFZuRQ$oYWtKMFNUBV30OHw7YjcdQ")
                .confirmed(false)
                .admin(false)
                .confirmationToken(confirmationToken)
                .build();

        User saved = this.userRepository.save(user);

        Assertions.assertFalse(saved.isConfirmed());

        User confirmed = this.confirmUser.execute(saved, confirmationToken);

        Assertions.assertTrue(confirmed.isConfirmed());
    }

    @Test
    public void execute_WhenTokenIsInvalid_ShouldThrowAndNotConfirm () {

        String confirmationToken = "mytoken";

        User user = User.builder()
                .email(this.testUtils.getRandomMail())
                .password("$argon2id$v=19$m=16,t=2,p=1$ZlJGMXBBejNwRmt6bFZuRQ$oYWtKMFNUBV30OHw7YjcdQ")
                .confirmed(false)
                .admin(false)
                .confirmationToken(confirmationToken)
                .build();

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

