package fr.esgi.secureupload.analysis.infrastructure.config;

import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisConfig {

    private final AnalysisRepository analysisJpaRepository;

    public AnalysisConfig(@Autowired AnalysisJpaRepository analysisJpaRepository){
        this.analysisJpaRepository = new AnalysisJpaRepositoryAdapter(analysisJpaRepository);
    }


}
