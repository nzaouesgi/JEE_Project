package fr.esgi.secureupload.analysis.infrastructure.controllers;

import fr.esgi.secureupload.analysis.usecases.CreateAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/analysis",
        produces = MediaType.APPLICATION_JSON_VALUE,
        name = "Analysis API"
)
public class AnalysisController {

    /*private Logger logger = LoggerFactory.getLogger(AnalysisController.class);

    private final CreateAnalysis startAnalysis;

    @Autowired
    public AnalysisController(@Autowired CreateAnalysis startAnalysis){
        this.startAnalysis = startAnalysis;
    }*/

}
