package fr.esgi.secureupload.analysis.usecases;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.entities.AnalysisStatus;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.helpers.VirusTotalAPIHandler;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.exceptions.FileNotFoundException;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class CreateAnalysis {

    private final AnalysisAPIHandler analysisAPI;
    private final AnalysisRepository analysisRepository;
    private final FileRepository fileRepository;

    public CreateAnalysis(AnalysisAPIHandler analysisAPI, AnalysisRepository analysisRepository, FileRepository fileRepository){
        this.analysisAPI = analysisAPI;
        this.analysisRepository = analysisRepository;
        this.fileRepository = fileRepository;
    }

    public void execute(String fileId){
        Objects.requireNonNull(fileId,"fileId can't be null");

        Analysis analysis = new Analysis();
        File file = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException(String.format("File with ID %s does not exist.", fileId)));
        analysis.setFile(file);
        analysis.setAnalysisStatus(AnalysisStatus.PENDING);

        this.analysisRepository.save(analysis);
    }
}
