package fr.esgi.secureupload.exceptions;

public class UserExceptions {

    /* User property validation failure */
    public static class PropertyValidationException extends RuntimeException {
        public PropertyValidationException (String message){
            super(message);
        }
    }

    /* Non existing user */
    public static class NotFoundException extends RuntimeException {
        public NotFoundException (String message){
            super(message);
        }
    }

    /* To be used when bad orderBy parameter is supplied */
    public static class PropertyNotFoundException extends RuntimeException {
        public PropertyNotFoundException (String message){
            super(message);
        }
    }

    /* Trying to create a account with existing mail */
    public static class MailAlreadyTakenException extends RuntimeException {
        public MailAlreadyTakenException (String message){
            super(message);
        }
    }

    /* Wrong password, unsafe password, private field related issues.  */
    public static class SecurityException extends RuntimeException {
        public SecurityException (String message){
            super(message);
        }
    }
}
