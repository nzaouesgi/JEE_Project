package fr.esgi.secureupload.users.exceptions;

/* Trying to create a account with existing mail */
public class UserMailAlreadyTakenException extends RuntimeException {
    public UserMailAlreadyTakenException (String message){
        super(message);
    }
}