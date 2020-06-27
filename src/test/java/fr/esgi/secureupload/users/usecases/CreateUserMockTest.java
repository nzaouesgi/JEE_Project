package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.dto.UserDTO;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserMailAlreadyTakenException;
import fr.esgi.secureupload.users.exceptions.UserPropertyValidationException;
import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import fr.esgi.secureupload.users.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.ports.UserFieldsValidator;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.Before;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateUserMockTest {

    private static final String goodMail = "user@domain.com";
    private static final String badMail = "not an email";

    private static final String existingMail = "exists@domain.com";

    private static final String goodPassword = "Password12345";
    private static final String badPassword = "abc";

    @Mock
    private UserRepository repository ;
    @Mock
    private UserPasswordEncoder encoder;
    @Mock
    private UserFieldsValidator validator;
    @Mock
    private RandomTokenGenerator generator;
    @Mock
    private ConfirmationMailSender sender;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void execute_ValidDto_ShouldCreateAndReturnUser (){

        when(repository.findByEmail(goodMail)).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        when(encoder.encode(any())).thenReturn(anyString());

        when(validator.validateMail(goodMail)).thenReturn(true);
        when(validator.validatePassword(goodPassword)).thenReturn(true);

        when(generator.generate(anyInt())).thenReturn(anyString());

        CreateUser createUser = new CreateUser(repository, encoder, sender, generator, validator);

        UserDTO userDto = new UserDTO();
        userDto.setEmail(goodMail);
        userDto.setPassword(goodPassword);

        Assertions.assertDoesNotThrow(() -> {
            User user = createUser.execute(userDto);
            Assertions.assertNotNull(user);
        });

        verify(repository).findByEmail(eq(userDto.getEmail()));
        verify(repository).save(any(User.class));
        verify(encoder).encode(eq(userDto.getPassword()));
        verify(validator).validateMail(eq(userDto.getEmail()));
        verify(generator).generate(anyInt());
        verify(sender).sendConfirmationMail(eq(userDto.getEmail().trim()), anyString());
    }

    @Test
    public void execute_BadMail_ShouldThrow (){

        when(validator.validateMail(badMail)).thenReturn(false);
        when(validator.validatePassword(badPassword)).thenReturn(false);

        CreateUser createUser = new CreateUser(repository, encoder, sender, generator, validator);

        UserDTO userDto = new UserDTO();
        userDto.setEmail(badMail);
        userDto.setPassword(badPassword);

        UserPropertyValidationException exception = Assertions.assertThrows(UserPropertyValidationException.class, () -> {
            createUser.execute(userDto);
        });

        Assertions.assertEquals(2, exception.getErrors().size());
    }

    @Test
    public void execute_ExistingMail_ShouldThrow (){

        when(validator.validateMail(existingMail)).thenReturn(true);
        when(validator.validatePassword(goodPassword)).thenReturn(true);

        when(repository.findByEmail(existingMail)).thenReturn(Optional.of(Mockito.mock(User.class)));

        CreateUser createUser = new CreateUser(repository, encoder, sender, generator, validator);

        UserDTO userDto = new UserDTO();
        userDto.setEmail(existingMail);
        userDto.setPassword(goodPassword);

        Assertions.assertThrows(UserMailAlreadyTakenException.class, () -> {
            createUser.execute(userDto);
        });
    }
}
