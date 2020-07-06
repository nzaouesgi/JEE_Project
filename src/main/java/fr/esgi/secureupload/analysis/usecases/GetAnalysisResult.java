package fr.esgi.secureupload.analysis.usecases;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;

import java.io.IOException;

public class GetAnalysisResult {
    private final AnalysisAPIHandler analysisAPI;
    private final AnalysisRepository analysisRepository;

    public GetAnalysisResult(AnalysisAPIHandler analysisAPI, AnalysisRepository analysisRepository){
        this.analysisAPI = analysisAPI;
        this.analysisRepository = analysisRepository;
    }

    public void execute(String scanId) throws IOException {
        Analysis analysis = this.analysisRepository.getByScanId(scanId);
        analysis = this.analysisAPI.getAnalysisResult(analysis);
        this.analysisRepository.save(analysis);
    }
}
