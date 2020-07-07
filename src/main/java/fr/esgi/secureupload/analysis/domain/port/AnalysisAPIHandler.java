package fr.esgi.secureupload.analysis.domain.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface AnalysisAPIHandler {

    String sendAnalysisRequest(String path) throws IOException;

    Analysis updateResult(Analysis analysis) throws JsonProcessingException;
}
