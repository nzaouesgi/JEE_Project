package fr.esgi.secureupload.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class ApiResponse {


    public static ResponseEntity<?> empty (HttpStatus status){
        return ResponseEntity.status(status).build();
    }

    public static <T> ResponseEntity<Data<T>> data (T data, HttpStatus status){
        Data<T> body = new Data<>();
        body.setData(data);
        body.setStatus(status.value());
        body.setTime(new Date());
        return ResponseEntity.status(status).body(body);
    }

    public static ResponseEntity<Error> error (String error, HttpStatus status){
        Error body = new Error();
        body.setError(error);
        body.setStatus(status.value());
        body.setTime(new Date());
        return ResponseEntity.status(status).body(body);
    }

    @lombok.Getter
    @lombok.Setter
    private static abstract class Body {
        private Date time;
        private int status;

    }

    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    private static class Data<T> extends Body {
        private T data;
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    private static class Error extends Body {
        private String error;
    }
}
