package fr.esgi.secureupload;

import fr.esgi.secureupload.entities.User;

import fr.esgi.secureupload.services.UserService;
import fr.esgi.secureupload.utils.Utils;
import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserService userService;

    static public String getRandomMail (){
        return String.format("%suser@domain.fr", Utils.randomString(3));
    }

    private List<User> toDelete = new ArrayList<>();

    @Test
    public void createValidUser (){
        User.UserBuilder builder = User.builder();

        Assertions.assertDoesNotThrow(() -> builder.email(UserTest.getRandomMail()));
        Assertions.assertDoesNotThrow(() -> builder.password(Utils.randomString(4)));

        User user = builder.build();

        Assertions.assertDoesNotThrow(() -> this.userService.save(user));
    }

    @Test
    public void rejectInvalidUser (){
        User.UserBuilder builder = User.builder();

        Assertions.assertThrows(User.PropertyValidationException.class, () -> builder.email("not an email"));
        Assertions.assertThrows(User.PropertyValidationException.class, () -> builder.password("123"));

        Assertions.assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void saveUser (){
        User user = User.builder()
                .email(this.getRandomMail())
                .password(Utils.randomString(4))
                .build();
        Assertions.assertDoesNotThrow(() -> {
            this.userService.save(user);
        });
        this.toDelete.add(user);
    }

    @Test
    public void deleteUser (){
        User user = User.builder()
                .email(this.getRandomMail())
                .password(Utils.randomString(4))
                .build();

        this.userService.save(user);

        Assertions.assertDoesNotThrow(() -> {
            this.userService.delete(user);
        });
    }

    @Test
    public void findByPattern (){
        User user = User.builder()
                .email(this.getRandomMail())
                .password(Utils.randomString(4))
                .build();

        this.userService.save(user);

        Assertions.assertTrue(this.userService.findAllByPattern(
                user.getEmail()
                        .substring(1), 0, 1, Sort.by("email"))
                        .getContent()
                        .stream()
                        .anyMatch(u -> u.getUuid().equals(user.getUuid())));
    }

    @After
    public void clean (){
        for (User user : this.toDelete){
            this.userService.delete(user);
        }
    }
}
