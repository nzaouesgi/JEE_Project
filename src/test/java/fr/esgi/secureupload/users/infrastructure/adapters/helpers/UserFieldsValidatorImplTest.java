package fr.esgi.secureupload.users.infrastructure.adapters.helpers;

import fr.esgi.secureupload.users.domain.ports.UserFieldsValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserFieldsValidatorImplTest {

    private final UserFieldsValidator validator;

    public UserFieldsValidatorImplTest(){
        this.validator = new UserFieldsValidatorImpl();
    }

    @Test
    public void validatePassword_WhenWeakPassword_ShouldReturnFalse (){
        Assertions.assertFalse(this.validator.validatePassword("abc"));
    }

    @Test
    public void validatePassword_WhenStrongPassword_ShouldReturnTrue (){
        Assertions.assertTrue(this.validator.validatePassword("GoodPassword12345"));
    }

    @Test
    public void validateEmail_WhenBadEmail_ShouldReturnFalse (){
        Assertions.assertFalse(this.validator.validateMail("not an email"));
    }

    @Test
    public void validateEmail_WhenValidEmail_ShouldReturnTrue (){
        Assertions.assertTrue(this.validator.validateMail("good@format.com"));
    }
}
