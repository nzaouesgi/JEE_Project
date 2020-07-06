package fr.esgi.secureupload.analysis.infrastructure.controllers;

import fr.esgi.secureupload.analysis.usecases.CreateAnalysis;
import fr.esgi.secureupload.analysis.usecases.GetAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/analysis",
        produces = MediaType.APPLICATION_JSON_VALUE,
        name = "Analysis API"
)
public class AnalysisController {

    private Logger logger = LoggerFactory.getLogger(AnalysisController.class);

    private final CreateAnalysis createAnalysis;

    private final GetAnalysisResult getAnalysisResult;

    @Autowired
    public AnalysisController(@Autowired CreateAnalysis createAnalysis,
                              @Autowired GetAnalysisResult getAnalysisResult){
        this.createAnalysis = createAnalysis;
        this.getAnalysisResult = getAnalysisResult;
    }

}
