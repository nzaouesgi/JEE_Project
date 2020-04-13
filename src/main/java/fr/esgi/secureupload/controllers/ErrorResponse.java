package fr.esgi.secureupload.controllers;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse  {
    private String error;
    public ErrorResponse (String error){
        this.error = error;
    }
    public ErrorResponse (Exception e){
        this.error = e.getMessage();
    }
}