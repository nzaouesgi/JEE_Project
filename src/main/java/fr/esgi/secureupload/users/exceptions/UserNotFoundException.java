package fr.esgi.secureupload.users.exceptions;

/* User was not found */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException (String message){
        super(message);
    }
}
