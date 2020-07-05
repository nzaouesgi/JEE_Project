package fr.esgi.secureupload.analysis.infrastructure.config;

import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepositoryAdapter;
import fr.esgi.secureupload.analysis.infrastructure.adapters.helpers.VirusTotalAPIHandler;
import fr.esgi.secureupload.analysis.usecases.CreateAnalysis;
import fr.esgi.secureupload.analysis.usecases.GetAnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisConfig {

    private final AnalysisRepository analysisJpaRepository;

    private final AnalysisAPIHandler analysisAPIHandler;

    public AnalysisConfig(@Autowired AnalysisJpaRepository analysisJpaRepository,
                          @Autowired VirusTotalAPIHandler virusTotalAPIHandler){
        this.analysisJpaRepository = new AnalysisJpaRepositoryAdapter(analysisJpaRepository);
        this.analysisAPIHandler = virusTotalAPIHandler;
    }

    @Bean
    public CreateAnalysis createAnalysis(){
        return new CreateAnalysis(this.analysisAPIHandler,this.analysisJpaRepository);
    }

    @Bean
    public GetAnalysisResult getAnalysisResult(){
        return new GetAnalysisResult(this.analysisAPIHandler, this.analysisJpaRepository);
    }

}
