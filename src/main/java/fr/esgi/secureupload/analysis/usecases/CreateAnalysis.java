package fr.esgi.secureupload.analysis.usecases;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.helpers.VirusTotalAPIHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class CreateAnalysis {

    private final AnalysisAPIHandler analysisAPI;
    private final AnalysisRepository analysisRepository;

    public CreateAnalysis(AnalysisAPIHandler analysisAPI, AnalysisRepository analysisRepository){
        this.analysisAPI = analysisAPI;
        this.analysisRepository = analysisRepository;
    }

    /*public void execute(MultipartFile file, Analysis analysis) throws IOException {
        String scanId = this.analysisAPI.sendAnalysisRequest(file);
        analysis.setScanId(scanId);
        this.analysisRepository.save(analysis);
    }*/
}
