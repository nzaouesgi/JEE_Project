package fr.esgi.secureupload.analysis.usecases;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.entities.AnalysisStatus;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.files.domain.entities.File;

import java.util.Objects;

public class CreateAnalysis {

    private final AnalysisRepository analysisRepository;


    public CreateAnalysis(AnalysisRepository analysisRepository){
        this.analysisRepository = analysisRepository;
    }

    public Analysis execute(File file){
        Objects.requireNonNull(file,"file can't be null");

        Analysis analysis = new Analysis();
        analysis.setFile(file);
        analysis.setAnalysisStatus(AnalysisStatus.PENDING);

        return this.analysisRepository.save(analysis);
    }
}
