package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /* User API errors */
    @ExceptionHandler({User.SecurityException.class})
    @ResponseBody
    public ResponseEntity<?> securityException(User.SecurityException e) {
        return ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({User.MailAlreadyTakenException.class})
    @ResponseBody
    public ResponseEntity<?> mailAlreadyTakenHandler(User.MailAlreadyTakenException e) {
        return ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({User.PropertyNotFoundException.class})
    @ResponseBody
    public ResponseEntity<?> userBadPropertyHandler(User.PropertyNotFoundException e) {
        return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({User.NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> userNotFoundHandler(User.NotFoundException e) {
        return ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /* Generic errors */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> exceptionHandler(Exception e) {
        e.printStackTrace();
        return ApiResponse.error("An error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
