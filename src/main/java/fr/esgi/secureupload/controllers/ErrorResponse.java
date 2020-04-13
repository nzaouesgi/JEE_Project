package fr.esgi.secureupload.controllers;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse  {
    private String error;
    private int status;
    public ErrorResponse (String error, HttpStatus status){
        this.error = error;
        this.status = status.value();
    }
}