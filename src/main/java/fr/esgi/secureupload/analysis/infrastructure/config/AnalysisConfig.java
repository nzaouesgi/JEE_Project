package fr.esgi.secureupload.analysis.infrastructure.config;

import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepositoryAdapter;
import fr.esgi.secureupload.analysis.infrastructure.adapters.helpers.VirusTotalApiHandler;
import fr.esgi.secureupload.analysis.usecases.*;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisConfig {

    private final AnalysisRepository analysisJpaRepository;

    private final AnalysisAPIHandler virusTotalApiHandler;

    public AnalysisConfig(@Autowired AnalysisJpaRepository analysisJpaRepository,
                          @Autowired VirusTotalApiHandler virusTotalApiHandler){
        this.analysisJpaRepository = new AnalysisJpaRepositoryAdapter(analysisJpaRepository);
        this.virusTotalApiHandler = virusTotalApiHandler;
    }

    @Bean
    public CreateAnalysis createAnalysis(){
        return new CreateAnalysis(this.analysisJpaRepository);
    }

    @Bean
    public FindAnalysisById findAnalysisById (){
        return new FindAnalysisById(this.analysisJpaRepository);
    }

    @Bean
    public GetAnalysisResult getAnalysisResult(){ return new GetAnalysisResult(this.virusTotalApiHandler, this.analysisJpaRepository); }

    @Bean
    public StartAnalysis startAnalysis (){ return new StartAnalysis(this.virusTotalApiHandler, this.analysisJpaRepository); }

    @Bean
    public FindAnalysisByFile findAnalysisByFile (){ return new FindAnalysisByFile(this.analysisJpaRepository, this.virusTotalApiHandler); }

}
