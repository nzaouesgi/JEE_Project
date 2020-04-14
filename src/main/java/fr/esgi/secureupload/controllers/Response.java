package fr.esgi.secureupload.controllers;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Response {

    @lombok.Getter
    @lombok.Setter
    public static class Body {
        private Date time;
        private int status;
        public Body(int status){
            this.setStatus(status);
            this.setTime(new Date());
        }
    }

    @lombok.Getter
    @lombok.Setter
    public static class DataBody<T> extends Body {
        private T data;
        public DataBody(T data, int status){
            super(status);
            this.data = data;
        }
    }

    @lombok.Getter
    @lombok.Setter
    public static class ErrorBody extends Body {
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
}
