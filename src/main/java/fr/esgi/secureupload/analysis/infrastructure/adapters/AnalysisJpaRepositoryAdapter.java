package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaAdapter;

public class AnalysisJpaRepositoryAdapter implements AnalysisRepository {

    private AnalysisJpaRepository analysisJpaRepository;

    public AnalysisJpaRepositoryAdapter(AnalysisJpaRepository analysisJpaRepository){
        this.analysisJpaRepository = analysisJpaRepository;
    }

    @Override
    public Analysis save(Analysis analysis) {
        return AnalysisJpaAdapter.convertToAnalysis(this.analysisJpaRepository.save(AnalysisJpaAdapter.convertToJpaAnalysis(analysis)));
    }
}
