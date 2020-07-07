package fr.esgi.secureupload.analysis.usecases;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.exceptions.AnalysisNotFoundException;

import java.util.Objects;

public class FindAnalysisById {

    private final AnalysisRepository analysisRepository;

    public FindAnalysisById( AnalysisRepository analysisRepository){
        this.analysisRepository = analysisRepository;
    }

    public Analysis execute(String id){
        Objects.requireNonNull(id,"id can't be null");
        return this.analysisRepository.findById(id)
                .orElseThrow(() -> new AnalysisNotFoundException(String.format("Analysis %s was not found", id)));
    }
}
