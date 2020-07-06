package fr.esgi.secureupload.analysis.usecases;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepositoryAdapter;

@SpringBootTest
public class StartAnalysisTest {

    private static final String SCAN_ID_MOCK = "12345";

    private final StartAnalysis startAnalysis;
    private final AnalysisRepository analysisRepository;

    public StartAnalysisTest(@Autowired AnalysisJpaRepository analysisRepository) {
        this.analysisRepository = new AnalysisJpaRepositoryAdapter(analysisRepository);
        this.startAnalysis = new StartAnalysis(new AnalysisAPIMock(), this.analysisRepository);
    }

    @Test
    public void execute_ShouldSaveAnalysis() {
        Assertions.assertDoesNotThrow(() -> {
            this.startAnalysis.execute("path");
        });

        var analysis = this.analysisRepository.getByScanId(SCAN_ID_MOCK);
        Assertions.assertNotNull(analysis);
    }

    class AnalysisAPIMock implements AnalysisAPIHandler {

        @Override
        public String sendAnalysisRequest(String path) throws IOException {
            return SCAN_ID_MOCK; // ScanID
        }

        @Override
        public Analysis getAnalysisResult(Analysis analysis) throws JsonProcessingException {
            return null;
        }

    }
}