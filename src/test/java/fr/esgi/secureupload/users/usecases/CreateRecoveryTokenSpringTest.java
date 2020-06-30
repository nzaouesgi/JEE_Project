package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.SpringTestWithUsers;
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
public class CreateRecoveryTokenSpringTest extends SpringTestWithUsers {

    private final CreateRecoveryToken createRecoveryToken;
    private final UserRepository repository;

    public CreateRecoveryTokenSpringTest (@Autowired CreateRecoveryToken createRecoveryToken, @Autowired UserJpaRepository jpaRepository){
        this.createRecoveryToken = createRecoveryToken;
        this.repository = new UserJpaRepositoryAdapter(jpaRepository);
    }

    @Test
    public void execute_WithUnconfirmedUser_ShouldThrow (){

        users.get(0).setConfirmed(false);
        users.set(0, this.repository.save(users.get(0)));

        Assertions.assertThrows(UserSecurityException.class, () -> {
            this.createRecoveryToken.execute(users.get(0));
        });
    }

    @Test
    public void execute_WithConfirmedUser_ShouldSetRecoveryTokenAndReturnUser (){

        users.get(0).setConfirmed(true);
        users.set(0, this.repository.save(users.get(0)));

        User toBeRecovered = this.createRecoveryToken.execute(users.get(0));

        Assertions.assertNotNull(toBeRecovered);
        Assertions.assertNotNull(toBeRecovered.getRecoveryToken());
    }
}
