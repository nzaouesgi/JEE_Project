package fr.esgi.secureupload.analysis.infrastructure.controllers;

import fr.esgi.secureupload.analysis.domain.exceptions.AnalysisNotFoundException;
import fr.esgi.secureupload.analysis.domain.exceptions.AnalysisRequestNotAccepted;
import fr.esgi.secureupload.common.infrastructure.controllers.response.ErrorBody;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AnalysisExceptionHandler {

    @ExceptionHandler({AnalysisNotFoundException.class})
    public ResponseEntity<ErrorBody> handleAnalysisNotFound(AnalysisNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

    @ExceptionHandler({AnalysisRequestNotAccepted.class})
    public ResponseEntity<ErrorBody> handleAnalysisRequestNotAccepted(AnalysisRequestNotAccepted e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorBody(e.getMessage(), status.value()), status);
    }

}
