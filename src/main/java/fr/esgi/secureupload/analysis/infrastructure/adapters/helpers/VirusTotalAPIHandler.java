package fr.esgi.secureupload.analysis.infrastructure.adapters.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.infrastructure.exceptions.AnalysisRequestNotAccepted;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VirusTotalAPIHandler implements AnalysisAPIHandler {

    private String apiKey;

    private final ObjectMapper objectMapper;

    public VirusTotalAPIHandler(String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String sendAnalysisRequest(MultipartFile file) throws IOException {
        String url = "https://www.virustotal.com/vtapi/v2/file/scan";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("apikey", this.apiKey);
        body.add("file", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        //Check if request is ok or not
        if(response.getStatusCode() != HttpStatus.ACCEPTED){
            throw new AnalysisRequestNotAccepted("Analysis request was not accepted by virus total");
        }

        JsonNode root = this.objectMapper.readTree(response.getBody());
        return root.get("scan_id").asText();
    }

    @Override
    public Analysis getAnalysisResult(Analysis analysis) throws JsonProcessingException {
        String url = "https://www.virustotal.com/vtapi/v2/file/report?apikey=" + apiKey + "&resource=" + analysis.getScanId();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if(response.getStatusCode() != HttpStatus.OK){
            //Need to create new exception
            throw new AnalysisRequestNotAccepted("Total virus api couldn't get out analysis");
        }

        JsonNode root = this.objectMapper.readTree(response.getBody());
        analysis.setPositiveScans(root.get("positives").asInt());
        analysis.setTotalScans(root.get("total").asInt());
        return analysis;
    }
}
