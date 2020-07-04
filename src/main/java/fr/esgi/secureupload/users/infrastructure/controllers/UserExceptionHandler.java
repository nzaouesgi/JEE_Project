package fr.esgi.secureupload.users.infrastructure.controllers;

import fr.esgi.secureupload.common.infrastructure.controllers.response.ErrorBody;
import fr.esgi.secureupload.users.domain.exceptions.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

    /* User API errors */
    @ExceptionHandler({UserPropertyValidationException.class})
    public ResponseEntity<ErrorBody> handleUserPropertyValidation(UserPropertyValidationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorBody(e.getErrors(), status.value()), status);
    }

    @ExceptionHandler({UserSecurityException.class})
    public ResponseEntity<ErrorBody> handleUserSecurity(UserSecurityException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

    @ExceptionHandler({UserMailAlreadyTakenException.class})
    public ResponseEntity<ErrorBody> handlerUserMailAlreadyTaken(UserMailAlreadyTakenException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

    @ExceptionHandler({UserPropertyNotFoundException.class})
    public ResponseEntity<ErrorBody> handleUserPropertyNotFound(UserPropertyNotFoundException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ErrorBody> handleUserNotFound(UserNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }
}
