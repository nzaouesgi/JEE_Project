package fr.esgi.secureupload.analysis.usecases;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;

import java.io.IOException;

public class StartAnalysis {

    private final AnalysisAPIHandler analysisAPI;
    private final AnalysisRepository analysisRepository;

    public StartAnalysis(AnalysisAPIHandler analysisAPI, AnalysisRepository analysisRepository){
        this.analysisAPI = analysisAPI;
        this.analysisRepository = analysisRepository;
    }

    public void execute(String path, String analysisId) throws IOException {
        String scanId = this.analysisAPI.sendAnalysisRequest(path);
        Analysis analysis = this.analysisRepository.getOne(analysisId);
        analysis.setScanId(scanId);
        this.analysisRepository.save(analysis);
    }

}
