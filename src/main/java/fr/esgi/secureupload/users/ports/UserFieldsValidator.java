package fr.esgi.secureupload.users.ports;

public interface UserFieldsValidator {
    boolean validateMail (String email);
    boolean validatePassword(String password);
}
