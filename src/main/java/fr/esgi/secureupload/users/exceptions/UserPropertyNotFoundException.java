package fr.esgi.secureupload.users.exceptions;

/* User property does not exist */
public class UserPropertyNotFoundException extends RuntimeException {
    public UserPropertyNotFoundException (String message){
        super(message);
    }
}
