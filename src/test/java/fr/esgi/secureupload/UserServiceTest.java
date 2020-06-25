package fr.esgi.secureupload;

import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserRepositoryAdapter;
import fr.esgi.secureupload.users.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;



@SpringBootTest
public class UserServiceTest {

    private List<User> toDelete = new ArrayList<>();

    private UserRepositoryAdapter userJpaRepositoryImpl;

    @Autowired
    private TestUtils testUtils;

    public UserServiceTest (@Autowired UserJpaRepository userJpaRepository){
        this.userJpaRepositoryImpl = new UserRepositoryAdapter(userJpaRepository);
    }

    @Test
    public void findById (){

    }

    @Test
    public void saveUser (){
        User user = this.testUtils.getRandomUser(false);
        Assertions.assertDoesNotThrow(() -> {
            this.userJpaRepositoryImpl.save(user);
        });
        this.toDelete.add(user);
    }

    @Test
    public void deleteUser (){
        User user = this.testUtils.getRandomUser(false);

        this.userJpaRepositoryImpl.save(user);

        Assertions.assertDoesNotThrow(() -> {
            this.userJpaRepositoryImpl.delete(user);
        });
    }

    @Test
    public void findByPattern (){
        User user = this.testUtils.getRandomUser(false);

        User saved = this.userJpaRepositoryImpl.save(user);

        Assertions.assertTrue(this.userJpaRepositoryImpl.findAllByPattern(saved.getEmail().substring(1), PageRequest.of(0, 1, Sort.by("email")))
                .getContent()
                .stream()
                .anyMatch(u -> u.getId().equals(saved.getId())));
    }

    @AfterEach
    public void clean(){
        for (User user : this.toDelete){
            this.userJpaRepositoryImpl.delete(user);
        }
    }
}
