package fr.esgi.secureupload.users.usecases;


import fr.esgi.secureupload.utils.TestUtils;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.infrastructure.dto.UserDTO;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserMailAlreadyTakenException;
import fr.esgi.secureupload.users.domain.exceptions.UserPropertyValidationException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreateUserTest {

    private final UserRepository userRepository;
    private final CreateUser createUser;
    private final TestUtils testUtils;

    public CreateUserTest(@Autowired CreateUser createUser, @Autowired UserJpaRepository userJpaRepository, @Autowired TestUtils testUtils){
        this.createUser = createUser;
        this.userRepository = new UserJpaRepositoryAdapter(userJpaRepository);
        this.testUtils = testUtils;
    }

    @Test
    public void execute_WithValidDTO_ShouldCreateUser (){

        UserDTO userDto = new UserDTO();
        userDto.setEmail("validemail@domain.com");
        userDto.setPassword("ValidPassword12345");

        Assertions.assertDoesNotThrow(() -> {
            User user = this.createUser.execute(userDto);
            Assertions.assertNotNull(user);
        });
    }

    @Test
    public void execute_WithAlreadyExistingMail_ShouldThrow (){

        User existing = this.userRepository.save(this.testUtils.getRandomUser(false));

        UserDTO userDto = new UserDTO();
        userDto.setEmail(existing.getEmail());
        userDto.setPassword("ValidPassword12345");

        Assertions.assertThrows(UserMailAlreadyTakenException.class, () -> {
            this.createUser.execute(userDto);
        });
    }

    @Test
    public void execute_WithBadEmail_ShouldThrow (){

        UserDTO userDto = new UserDTO();
        userDto.setEmail("not a mail address");
        userDto.setPassword("rrrrr");

        UserPropertyValidationException exception = Assertions.assertThrows(UserPropertyValidationException.class, () -> {
            this.createUser.execute(userDto);
        });

        Assertions.assertEquals(2, exception.getErrors().size());
    }
}
