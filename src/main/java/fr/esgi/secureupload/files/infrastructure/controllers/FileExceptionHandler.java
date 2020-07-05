package fr.esgi.secureupload.files.infrastructure.controllers;

import fr.esgi.secureupload.common.infrastructure.controllers.response.ErrorBody;
import fr.esgi.secureupload.files.domain.exceptions.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileExceptionHandler {

    /* Files API errors */
    @ExceptionHandler({FileNotFoundException.class})
    public ResponseEntity<ErrorBody> handleFileNotFound(FileNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

    @ExceptionHandler({EmptyFileException.class})
    public ResponseEntity<ErrorBody> handleEmptyFile(EmptyFileException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

    @ExceptionHandler({FileSecurityException.class})
    public ResponseEntity<ErrorBody> handleFileSecurity(FileSecurityException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

    @ExceptionHandler({ConversionFailedException.class})
    protected ResponseEntity<?> handleConversionFailed(ConversionFailedException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorBody(e.getValue() + " cannot be converted.", status.value()));
    }

}
