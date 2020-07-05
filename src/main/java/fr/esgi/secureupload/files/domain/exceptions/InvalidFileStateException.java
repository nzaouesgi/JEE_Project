package fr.esgi.secureupload.files.domain.exceptions;

public class InvalidFileStateException extends RuntimeException{
    public InvalidFileStateException(String message){ super(message); }
}
