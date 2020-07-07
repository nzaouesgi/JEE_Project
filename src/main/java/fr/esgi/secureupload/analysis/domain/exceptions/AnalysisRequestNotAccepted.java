package fr.esgi.secureupload.analysis.domain.exceptions;

public class AnalysisRequestNotAccepted extends RuntimeException{
    public AnalysisRequestNotAccepted(String message){
        super(message);
    }
}