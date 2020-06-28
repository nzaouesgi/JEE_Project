package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeleteUserSpringTest extends SpringTestWithUsers {

    @Autowired
    private DeleteUser deleteUser;

    @Test
    public void execute_WhenValidId_ShouldDeleteUser (){
        Assertions.assertDoesNotThrow(() -> {
            deleteUser.execute(users.get(0).getId());
            deleteFromUsers(0);
        });
    }

    @Test
    public void execute_WhenInvalidId_ShouldThrow (){
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            deleteUser.execute("wrongid");
        });
    }
}
