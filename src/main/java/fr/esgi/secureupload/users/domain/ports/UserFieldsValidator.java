package fr.esgi.secureupload.users.domain.ports;

public interface UserFieldsValidator {
    boolean validateMail (String email);
    boolean validatePassword(String password);
}
