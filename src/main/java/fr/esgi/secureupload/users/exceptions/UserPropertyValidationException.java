package fr.esgi.secureupload.users.exceptions;

/* User property validation failure */
public class UserPropertyValidationException extends RuntimeException {
    public UserPropertyValidationException(String message){
        super(message);
    }
}