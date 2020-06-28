package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FindUserByEmailSpringTest extends SpringTestWithUsers {

    @Autowired
    private FindUserByEmail findUserByEmail;

    @Test
    public void execute_ShouldReturnUserById (){
        Assertions.assertDoesNotThrow(() -> {
            User found = this.findUserByEmail.execute(users.get(0).getEmail());
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

