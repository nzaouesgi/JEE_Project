package fr.esgi.secureupload.analysis.usecases;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.files.domain.entities.File;

import java.util.List;
import java.util.Objects;

public class FindAnalysisByFile {

    private final AnalysisRepository analysisRepository;
    private final AnalysisAPIHandler analysisApiHandler;

    public FindAnalysisByFile( AnalysisRepository analysisRepository, AnalysisAPIHandler analysisApiHandler){
        this.analysisRepository = analysisRepository;
        this.analysisApiHandler = analysisApiHandler;
    }

    public List<Analysis> execute(File file) throws JsonProcessingException {

        Objects.requireNonNull(file,"file can't be null");

        List<Analysis> analysis = this.analysisRepository.findAnalysisByFile(file);

        for (int i = 0; i < analysis.size(); i ++){
            analysis.set(i, this.analysisApiHandler.updateResult(analysis.get(i)));
        }

        return analysis;
    }
}
