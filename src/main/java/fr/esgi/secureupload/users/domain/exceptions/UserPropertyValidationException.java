package fr.esgi.secureupload.users.domain.exceptions;

import java.util.List;

/* User property validation failure */
public class UserPropertyValidationException extends RuntimeException {

    private List<String> errors;

    public UserPropertyValidationException(List<String> errors){
        super("Bad parameters were supplied.");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}