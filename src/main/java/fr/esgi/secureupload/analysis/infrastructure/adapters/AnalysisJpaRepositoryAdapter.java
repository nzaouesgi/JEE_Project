package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaAdapter;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaAdapter;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnalysisJpaRepositoryAdapter implements AnalysisRepository {

    private AnalysisJpaRepository analysisJpaRepository;
    private AnalysisJpaAdapter analysisJpaAdapter;
    private FileJpaAdapter fileJpaAdapter;

    public AnalysisJpaRepositoryAdapter(FileJpaRepository fileJpaRepository, AnalysisJpaRepository analysisJpaRepository, UserJpaRepository userJpaRepository){
        this.analysisJpaRepository = analysisJpaRepository;
        this.fileJpaAdapter = new FileJpaAdapter(fileJpaRepository, new UserJpaAdapter(userJpaRepository));
        this.analysisJpaAdapter = new AnalysisJpaAdapter(analysisJpaRepository, this.fileJpaAdapter);
    }

    @Override
    public Analysis save(Analysis analysis) {
        return this.analysisJpaAdapter.convertToAnalysis(this.analysisJpaRepository.save(this.analysisJpaAdapter.convertToJpaAnalysis(analysis)));
    }

    public Optional<Analysis> findByScanId(String scanId){
        return this.analysisJpaRepository.findByScanId(scanId).map(this.analysisJpaAdapter::convertToAnalysis);
    }

    @Override
    public void deleteById(String id) {
        this.analysisJpaRepository.deleteById(id);
    }

    @Override
    public List<Analysis> findAnalysisByFile(File file) {
        return this.analysisJpaRepository.findByFile(this.fileJpaAdapter.convertToJpaEntity(file))
                .stream()
                .map(this.analysisJpaAdapter::convertToAnalysis)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Analysis> findById(String id) {
        return this.analysisJpaRepository.findById(id)
                .map(this.analysisJpaAdapter::convertToAnalysis);
    }


}
