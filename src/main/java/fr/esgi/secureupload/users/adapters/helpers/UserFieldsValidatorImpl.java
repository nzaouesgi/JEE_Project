package fr.esgi.secureupload.users.adapters.helpers;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.ports.UserFieldsValidator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class UserFieldsValidatorImpl implements UserFieldsValidator {

    @Override
    public boolean validateMail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validatePassword(String password) {
        return password.length() >= User.PASSWORD_MIN_LENGTH && password.length() <= User.PASSWORD_MAX_LENGTH;
    }
}
