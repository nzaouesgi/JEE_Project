package fr.esgi.secureupload.users.exceptions;

/* Wrong password, unsafe password, private field related issues.  */
public class UserSecurityException extends RuntimeException {
    public UserSecurityException (String message){
        super(message);
    }
}
