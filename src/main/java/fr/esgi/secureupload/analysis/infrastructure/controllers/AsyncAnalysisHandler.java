package fr.esgi.secureupload.analysis.infrastructure.controllers;

import fr.esgi.secureupload.analysis.usecases.GetAnalysisResult;
import fr.esgi.secureupload.analysis.usecases.StartAnalysis;
import fr.esgi.secureupload.files.infrastructure.controllers.AsyncFileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;

public class AsyncAnalysisHandler {

    private Logger logger = LoggerFactory.getLogger(AsyncAnalysisHandler.class);

    private final StartAnalysis startAnalysis;
    private final GetAnalysisResult getAnalysisResult;

    public AsyncAnalysisHandler(@Autowired StartAnalysis startAnalysis,
                                @Autowired GetAnalysisResult getAnalysisResult){
        this.startAnalysis = startAnalysis;
        this.getAnalysisResult = getAnalysisResult;
    }

    @Async
    public void startAnalysis(String path, String id) throws IOException {
        logger.info(String.format("send analysis (%s) request", id));
        this.startAnalysis.execute(path, id);
        logger.info(String.format("analysis (%s) in progress ...", id));
    }

    @Async
    public void getAnalysis(String scanId) throws IOException {
        logger.info(String.format("send analysis (scanId : %s) request for result", scanId));
        this.getAnalysisResult.execute(scanId);
        logger.info(String.format("analysis (scanId : %s) result received"));
    }


}
