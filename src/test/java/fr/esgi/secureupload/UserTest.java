package fr.esgi.secureupload;

import fr.esgi.secureupload.entities.User;

import fr.esgi.secureupload.exceptions.UserExceptions;
import fr.esgi.secureupload.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UserTest {

    @Autowired
    private TestUtils testUtils;

    @Test
    public void createValidUser (){
        User.UserBuilder builder = User.builder();

        Assertions.assertDoesNotThrow(() -> builder.email(testUtils.getRandomMail()));
        Assertions.assertDoesNotThrow(() -> builder.password(Utils.randomBytesToHex(4)));

        Assertions.assertDoesNotThrow(builder::build);
    }

    @Test
    public void rejectInvalidUser (){
        User.UserBuilder builder = User.builder();

        Assertions.assertThrows(UserExceptions.PropertyValidationException.class, () -> builder.email("not an email"));
        Assertions.assertThrows(UserExceptions.PropertyValidationException.class, () -> builder.password("123"));

        Assertions.assertThrows(NullPointerException.class, builder::build);
    }

}
