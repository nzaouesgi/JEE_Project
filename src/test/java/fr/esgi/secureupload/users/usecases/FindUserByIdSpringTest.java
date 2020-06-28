package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FindUserByIdSpringTest extends SpringTestWithUsers {

    @Autowired
    private FindUserById findUserById;

    @Test
    public void execute_ShouldReturnUserById (){

        Assertions.assertDoesNotThrow(() -> {
            User found = this.findUserById.execute(users.get(0).getId());
            Assertions.assertNotNull(found);
        });
    }

    @Test
    public void execute_ShouldThrowWhenNotFound (){
        Assertions.assertThrows(UserNotFoundException.class, () -> {
           this.findUserById.execute("invalid id");
        });
    }
}
