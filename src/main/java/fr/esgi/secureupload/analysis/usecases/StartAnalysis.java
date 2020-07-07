package fr.esgi.secureupload.analysis.usecases;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.entities.AnalysisStatus;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.files.domain.entities.File;

import java.io.IOException;

public class StartAnalysis {

    private final AnalysisAPIHandler analysisAPI;
    private final AnalysisRepository analysisRepository;

    public StartAnalysis(AnalysisAPIHandler analysisAPI, AnalysisRepository analysisRepository){
        this.analysisAPI = analysisAPI;
        this.analysisRepository = analysisRepository;
    }

    public void execute(String path, Analysis analysis) {

        analysis.setAnalysisStatus(AnalysisStatus.PENDING);
        this.analysisRepository.save(analysis);

        try {
            String scanId = this.analysisAPI.sendAnalysisRequest(path);
            analysis.setAnalysisStatus(AnalysisStatus.IN_PROGRESS);
            analysis.setScanId(scanId);
        } catch (Exception e){
            analysis.setAnalysisStatus(AnalysisStatus.FAILED);
        }

        this.analysisRepository.save(analysis);
    }

}
