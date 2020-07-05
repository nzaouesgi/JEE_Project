package fr.esgi.secureupload.analysis.infrastructure.exceptions;

public class AnalysisRequestNotAccepted extends RuntimeException{
    public AnalysisRequestNotAccepted(String message){
        super(message);
    }
}