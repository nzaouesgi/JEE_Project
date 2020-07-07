package fr.esgi.secureupload.analysis.infrastructure.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.entities.AnalysisStatus;
import fr.esgi.secureupload.analysis.infrastructure.dto.CreateAnalysisDTO;
import fr.esgi.secureupload.analysis.usecases.CreateAnalysis;
import fr.esgi.secureupload.analysis.usecases.FindAnalysisByFile;
import fr.esgi.secureupload.analysis.usecases.FindAnalysisById;
import fr.esgi.secureupload.analysis.usecases.GetAnalysisResult;
import fr.esgi.secureupload.common.infrastructure.controllers.response.DataBody;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.exceptions.FileSecurityException;
import fr.esgi.secureupload.files.usecases.FindFileById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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
    private final FindAnalysisById findAnalysisById;
    private final FindFileById findFileById;
    private final AsyncAnalysisHandler asyncHandler;
    private final FindAnalysisByFile findAnalysisByFile;

    @Autowired
    public AnalysisController(@Autowired CreateAnalysis createAnalysis,
                              @Autowired FindFileById findFileById,
                              @Autowired GetAnalysisResult getAnalysisResult,
                              @Autowired AsyncAnalysisHandler asyncHandler,
                              @Autowired FindAnalysisById findAnalysisById,
                              @Autowired FindAnalysisByFile findAnalysisByFile){
        this.createAnalysis = createAnalysis;
        this.getAnalysisResult = getAnalysisResult;
        this.asyncHandler = asyncHandler;
        this.findFileById = findFileById;
        this.findAnalysisById = findAnalysisById;
        this.findAnalysisByFile = findAnalysisByFile;
    }

    private void checkIfFileBelongsToSelf (File file){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!file.getOwner().getId().equals(userId)){
            throw new FileSecurityException("This file belongs to another user.");
        }
    }

    @PostMapping
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<DataBody<Analysis>> createAnalysis(@RequestBody @Valid CreateAnalysisDTO createAnalysisDto, HttpServletResponse response) throws IOException {

        var status = HttpStatus.CREATED;
        var file = this.findFileById.execute(createAnalysisDto.getFileId());

        this.checkIfFileBelongsToSelf(file);

        var analysis = this.createAnalysis.execute(file);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(analysis.getId())
                .toUri();

        response.setHeader(HttpHeaders.LOCATION, uri.toString());

        this.asyncHandler.startAnalysis(analysis);

        return new ResponseEntity<>(new DataBody<>(analysis, status.value()), HttpStatus.CREATED);
    }

    @GetMapping
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<DataBody<List<Analysis>>> getFileAnalysis (@RequestParam(name = "fileId") String fileId) throws JsonProcessingException {

        HttpStatus status = HttpStatus.OK;
        File file = this.findFileById.execute(fileId);

        this.checkIfFileBelongsToSelf(file);

        return new ResponseEntity<>(new DataBody<>(this.findAnalysisByFile.execute(file), status.value()), status);
    }

    @GetMapping(value = "/{analysisId}")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<DataBody<Analysis>> getAnalysis ( @PathVariable(name = "analysisId") String analysisId) throws IOException {

        HttpStatus status = HttpStatus.OK;
        Analysis analysis = this.findAnalysisById.execute(analysisId);
        this.checkIfFileBelongsToSelf(analysis.getFile());

        if (analysis.getAnalysisStatus() == AnalysisStatus.IN_PROGRESS){
            analysis = this.getAnalysisResult.execute(analysis);
        }

        return new ResponseEntity<>(new DataBody<>(analysis, status.value()), status);
    }
}
