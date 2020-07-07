package fr.esgi.secureupload.analysis.infrastructure.controllers;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.usecases.GetAnalysisResult;
import fr.esgi.secureupload.analysis.usecases.StartAnalysis;
import fr.esgi.secureupload.files.usecases.FetchFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class AsyncAnalysisHandler {

    private Logger logger = LoggerFactory.getLogger(AsyncAnalysisHandler.class);

    private final StartAnalysis startAnalysis;
    private final FetchFile fetchFile;

    public AsyncAnalysisHandler(@Autowired StartAnalysis startAnalysis,
                                @Autowired GetAnalysisResult getAnalysisResult,
                                @Autowired FetchFile fetchFile){
        this.startAnalysis = startAnalysis;
        this.fetchFile = fetchFile;
    }

    @Async
    @Transactional
    public void startAnalysis (Analysis analysis) throws IOException {
        String path = this.fetchFile.execute(analysis.getFile().getId());
        this.startAnalysis.execute(path, analysis);
        Files.deleteIfExists(Paths.get(path));
    }
}
