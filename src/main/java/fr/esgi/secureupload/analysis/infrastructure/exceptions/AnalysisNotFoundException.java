package fr.esgi.secureupload.analysis.infrastructure.exceptions;

public class AnalysisNotFoundException extends RuntimeException {
    public AnalysisNotFoundException (String message ){
        super(message);
    }
}
