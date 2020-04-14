package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /* User API errors */
    @ExceptionHandler({User.PropertyValidationException.class})
    public ResponseEntity<?> propertyValidationExceptionHandler(User.PropertyValidationException e) {
        return ApiResponse.error(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({User.MailAlreadyTakenException.class, User.SecurityException.class})
    public ResponseEntity<?> mailAlreadyTakenHandler(User.MailAlreadyTakenException e) {
        return ApiResponse.error(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({User.PropertyNotFoundException.class})
    public ResponseEntity<?> userBadPropertyHandler(User.PropertyNotFoundException e) {
        return ApiResponse.error(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({User.NotFoundException.class})
    public ResponseEntity<?> userNotFoundHandler(User.NotFoundException e) {
        return ApiResponse.error(e, HttpStatus.NOT_FOUND);
    }

    /* Generic errors */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> exceptionHandler(Exception e) {
        e.printStackTrace();
        return ApiResponse.error(new Exception("An error occurred."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
