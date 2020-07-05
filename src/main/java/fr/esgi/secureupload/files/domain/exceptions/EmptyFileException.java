package fr.esgi.secureupload.files.domain.exceptions;

public class EmptyFileException extends RuntimeException{
    public EmptyFileException(String message){ super(message); }
}
