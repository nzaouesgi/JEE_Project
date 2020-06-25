package fr.esgi.secureupload;

import fr.esgi.secureupload.users.entities.User;

import fr.esgi.secureupload.common.utils.Utils;
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
        User.Builder builder = User.builder();

        Assertions.assertDoesNotThrow(() -> builder.email(testUtils.getRandomMail()));
        Assertions.assertDoesNotThrow(() -> builder.password(Utils.randomBytesToHex(4)));
        Assertions.assertDoesNotThrow(() -> builder.confirmationToken(Utils.randomBytesToHex(32)));

        Assertions.assertDoesNotThrow(builder::build);
    }

    @Test
    public void rejectInvalidUser (){
        User.Builder builder = User.builder();

        //Assertions.assertThrows(UserPropertyValidationException.class, () -> builder.email("not an email"));
        //Assertions.assertThrows(UserPropertyValidationException.class, () -> builder.password("123"));

        Assertions.assertThrows(IllegalArgumentException.class, builder::build);
    }

}
