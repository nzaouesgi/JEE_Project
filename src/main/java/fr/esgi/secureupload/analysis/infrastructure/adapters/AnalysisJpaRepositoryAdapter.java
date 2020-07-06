package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaAdapter;

import javax.swing.text.html.Option;
import java.util.Optional;

public class AnalysisJpaRepositoryAdapter implements AnalysisRepository {

    private AnalysisJpaRepository analysisJpaRepository;

    public AnalysisJpaRepositoryAdapter(AnalysisJpaRepository analysisJpaRepository){
        this.analysisJpaRepository = analysisJpaRepository;
    }

    @Override
    public Analysis save(Analysis analysis) {
        return AnalysisJpaAdapter.convertToAnalysis(this.analysisJpaRepository.save(AnalysisJpaAdapter.convertToJpaAnalysis(analysis)));
    }

    public Optional<Analysis> findByScanId(String scanId){
        return this.analysisJpaRepository.findByScanId(scanId).map(AnalysisJpaAdapter::convertToAnalysis);
    }

    @Override
    public void deleteById(String id) {
        this.analysisJpaRepository.deleteById(id);
    }

    @Override
    public Analysis getOne(String id) {
        return AnalysisJpaAdapter.convertToAnalysis(this.analysisJpaRepository.getOne(id));
    }

    @Override
    public Optional<Analysis> findById(String id) {
        return this.analysisJpaRepository.findById(id)
                .map(AnalysisJpaAdapter::convertToAnalysis);
    }


}
