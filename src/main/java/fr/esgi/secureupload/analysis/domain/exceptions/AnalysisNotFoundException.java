package fr.esgi.secureupload.analysis.domain.exceptions;

public class AnalysisNotFoundException extends RuntimeException {
    public AnalysisNotFoundException (String message ){
        super(message);
    }
}
