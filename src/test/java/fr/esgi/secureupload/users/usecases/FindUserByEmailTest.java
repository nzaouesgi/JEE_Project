package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.TestUtils;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FindUserByEmailTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private FindUserByEmail findUserByEmail;

    private UserRepository repository;

    public FindUserByEmailTest (@Autowired UserJpaRepository userJpaRepository){
        this.repository = new UserJpaRepositoryAdapter(userJpaRepository);
    }

    @Test
    public void execute_ShouldReturnUserById (){

        User user = testUtils.getRandomUser(false);

        User saved = this.repository.save(user);

        Assertions.assertDoesNotThrow(() -> {
            User found = this.findUserByEmail.execute(saved.getEmail());
            Assertions.assertNotNull(found);
        });
    }

    @Test
    public void execute_ShouldThrowWhenNotFound (){
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.findUserByEmail.execute("invalid email");
        });
    }
}
