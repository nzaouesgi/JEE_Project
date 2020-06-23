package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.exceptions.UserExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    public Response.ErrorBody setStatusAndCreateErrorBody(Exception e, HttpServletResponse response, int status){
        response.setStatus(status);
        return new Response.ErrorBody(e.getMessage(), response.getStatus());
    }

    public Response.ErrorBody setStatusAndCreateErrorBody(String e, HttpServletResponse response, int status){
        response.setStatus(status);
        return new Response.ErrorBody(e, response.getStatus());
    }

    /* Login exceptions */
    @ExceptionHandler({BadCredentialsException.class})
    @ResponseBody
    public Response.ErrorBody handleBadCredentialsException(BadCredentialsException e, HttpServletResponse response) {
        return setStatusAndCreateErrorBody("Login failed.", response, HttpStatus.FORBIDDEN.value());
    }

    /* User API errors */
    @ExceptionHandler({UserExceptions.PropertyValidationException.class})
    @ResponseBody
    public Response.ErrorBody handleUserPropertyValidation(UserExceptions.PropertyValidationException e, HttpServletResponse response) {
        return setStatusAndCreateErrorBody(e, response, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({UserExceptions.SecurityException.class})
    @ResponseBody
    public Response.ErrorBody handleUserSecurity(UserExceptions.SecurityException e, HttpServletResponse response) {

        return setStatusAndCreateErrorBody(e, response, HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler({UserExceptions.MailAlreadyTakenException.class})
    @ResponseBody
    public Response.ErrorBody handlerUserMailAlreadyTaken(UserExceptions.MailAlreadyTakenException e, HttpServletResponse response) {
        return setStatusAndCreateErrorBody(e, response, HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler({UserExceptions.PropertyNotFoundException.class})
    @ResponseBody
    public Response.ErrorBody handleUserPropertyNotFound(UserExceptions.PropertyNotFoundException e, HttpServletResponse response) {
        return setStatusAndCreateErrorBody(e, response, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({UserExceptions.NotFoundException.class})
    @ResponseBody
    public Response.ErrorBody handleUserNotFound(UserExceptions.NotFoundException e, HttpServletResponse response) {
        return setStatusAndCreateErrorBody(e, response, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({ AccessDeniedException.class})
    @ResponseBody
    public Response.ErrorBody handleAccessDenied(AccessDeniedException e, HttpServletResponse response) {
        return setStatusAndCreateErrorBody("Access denied.", response, HttpStatus.UNAUTHORIZED.value());
    }

    /* Generic MVC exceptions */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Response.ErrorBody handleException(Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        this.logger.error(String.format("Unhandled exception: %s (%s).", e.getClass(), e.getMessage()));
        return new Response.ErrorBody(Collections.singletonList("An internal server error occurred."), response.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException e, HttpHeaders headers, HttpStatus status, WebRequest request){
        return ResponseEntity.status(status).body(new Response.ErrorBody(e.getMessage(), status.value()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request){
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(new Response.ErrorBody(errors, status.value()));
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException e, HttpHeaders headers, HttpStatus status, WebRequest request){
        return ResponseEntity.status(status).body(new Response.ErrorBody(e.getMessage(), status.value()));
    }

    /* For all the rest of exceptions */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request){
        return ResponseEntity.status(status).body(new Response.ErrorBody(e.getMessage(), status.value()));
    }
}
