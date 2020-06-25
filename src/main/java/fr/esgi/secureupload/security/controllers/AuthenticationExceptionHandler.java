package fr.esgi.secureupload.security.controllers;

import fr.esgi.secureupload.common.controllers.response.ErrorBody;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationExceptionHandler {

    /* Login exceptions */
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ErrorBody> handleBadCredentialsException() {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(new ErrorBody("Login failed.", status.value()), status);
    }

    @ExceptionHandler({ AccessDeniedException.class})
    public ResponseEntity<ErrorBody> handleAccessDenied() {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(new ErrorBody("Access denied.", status.value()), status);
    }
}
