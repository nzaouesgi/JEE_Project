package fr.esgi.secureupload.analysis.infrastructure.adapters.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.entities.AnalysisStatus;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.infrastructure.exceptions.AnalysisRequestNotAccepted;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Component
public class VirusTotalApiHandler implements AnalysisAPIHandler {

    @Value("${secureupload.virustotal.api.key}")
    private String apiKey;

    private static final String API_KEY_QUERY_PARAM = "apikey";
    private static final String FILE_QUERY_PARAM = "file";

    private static final String VIRUS_TOTAL_URL_FILE_API = "https://www.virustotal.com/vtapi/v2/file";
    private static final String SCAN_ENDPOINT = "/scan";
    private static final String RESULT_ENDPOINT = "/report";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String sendAnalysisRequest(String path) throws IOException {
        String url = VIRUS_TOTAL_URL_FILE_API + SCAN_ENDPOINT;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        FileSystemResource file = new FileSystemResource(path);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(API_KEY_QUERY_PARAM, this.apiKey);
        body.add(FILE_QUERY_PARAM, file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        //Check if request is ok or not
        if(response.getStatusCode() != HttpStatus.OK){
            throw new AnalysisRequestNotAccepted("Analysis request was not accepted by virus total");
        }

        JsonNode root = this.objectMapper.readTree(Objects.requireNonNull(response.getBody(), "JSON API response is null"));
        return root.get("scan_id").asText();
    }

    @Override
    public Analysis updateResult(Analysis analysis) throws JsonProcessingException {

        String url = VIRUS_TOTAL_URL_FILE_API + RESULT_ENDPOINT + "?apikey=" + apiKey + "&resource=" + analysis.getScanId();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {

            JsonNode root = this.objectMapper.readTree(Objects.requireNonNull(response.getBody(), "JSON API response is null"));
            int responseCode = root.get("response_code").asInt();

            if (analysis.getAnalysisStatus() != AnalysisStatus.PENDING && responseCode == 0) {
                analysis.setAnalysisStatus(AnalysisStatus.FAILED);
            }

            else if (responseCode == 1) {
                analysis.setAnalysisStatus(AnalysisStatus.DONE);
                analysis.setPositiveScans(root.get("positives").asInt());
                analysis.setTotalScans(root.get("total").asInt());
            }
        }

        return analysis;
    }
}
