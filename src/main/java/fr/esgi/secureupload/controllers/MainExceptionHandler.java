package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MainExceptionHandler extends ResponseEntityExceptionHandler {

    /* User API errors */
    @ExceptionHandler({User.SecurityException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse securityException(User.SecurityException e) {
        return new ErrorResponse(e);
    }

    @ExceptionHandler({User.MailAlreadyTakenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse mailAlreadyTakenHandler(User.MailAlreadyTakenException e) {
        return new ErrorResponse(e);
    }

    @ExceptionHandler({User.PropertyNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse userBadPropertyHandler(User.PropertyNotFoundException e) {
        return new ErrorResponse(e);
    }

    @ExceptionHandler({User.NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse userNotFoundHandler(User.NotFoundException e) {
        return new ErrorResponse(e);
    }

    /* Generic errors */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse exceptionHandler(Exception e) {
        e.printStackTrace();
        return new ErrorResponse("Internal server error");
    }
}
