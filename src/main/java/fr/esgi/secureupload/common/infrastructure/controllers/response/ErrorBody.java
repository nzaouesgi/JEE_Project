package fr.esgi.secureupload.common.infrastructure.controllers.response;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
public class ErrorBody extends Body {
    private List<String> errors;
    public ErrorBody(String message, int status){
        super(status);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }
    public ErrorBody(List<String> messages, int status){
        super(status);
        this.errors = messages;
    }
}