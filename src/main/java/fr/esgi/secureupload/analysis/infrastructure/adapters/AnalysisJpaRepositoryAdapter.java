package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;

public class AnalysisJpaRepositoryAdapter implements AnalysisRepository {

    private AnalysisJpaRepository analysisJpaRepository;

    public AnalysisJpaRepositoryAdapter(AnalysisJpaRepository analysisJpaRepository){
        this.analysisJpaRepository = analysisJpaRepository;
    }

}
