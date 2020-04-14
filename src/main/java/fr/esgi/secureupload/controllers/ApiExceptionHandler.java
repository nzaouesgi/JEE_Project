package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /* User API errors */
    @ExceptionHandler({User.PropertyValidationException.class})
    @ResponseBody
    public Response.ErrorBody handleUserPropertyValidation(User.PropertyValidationException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new Response.ErrorBody(Collections.singletonList(e.getMessage()), response.getStatus());
    }

    @ExceptionHandler({User.SecurityException.class})
    @ResponseBody
    public Response.ErrorBody handleUserSecurity(User.SecurityException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return new Response.ErrorBody(Collections.singletonList(e.getMessage()), response.getStatus());
    }

    @ExceptionHandler({User.MailAlreadyTakenException.class})
    @ResponseBody
    public Response.ErrorBody handlerUserMailAlreadyTaken(User.MailAlreadyTakenException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return new Response.ErrorBody(Collections.singletonList(e.getMessage()), response.getStatus());
    }

    @ExceptionHandler({User.PropertyNotFoundException.class})
    @ResponseBody
    public Response.ErrorBody handleUserPropertyNotFound(User.PropertyNotFoundException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new Response.ErrorBody(Collections.singletonList(e.getMessage()), response.getStatus());
    }

    @ExceptionHandler({User.NotFoundException.class})
    @ResponseBody
    public Response.ErrorBody handleUserNotFound(User.NotFoundException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return new Response.ErrorBody(Collections.singletonList(e.getMessage()), response.getStatus());
    }

    /* Generic errors */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Response.ErrorBody exceptionHandler(Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        this.logger.error(String.format("Unhandled exception: %s (%s).", e.getClass(), e.getMessage()));
        return new Response.ErrorBody(Collections.singletonList("An internal server error occurred."), response.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request){

        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(new Response.ErrorBody(errors, status.value()));
    }
}
