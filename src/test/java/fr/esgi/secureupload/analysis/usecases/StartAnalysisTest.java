package fr.esgi.secureupload.analysis.usecases;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.domain.entities.AnalysisStatus;
import fr.esgi.secureupload.analysis.domain.port.AnalysisAPIHandler;
import fr.esgi.secureupload.analysis.domain.repository.AnalysisRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepository;
import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaRepositoryAdapter;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepositoryAdapter;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;

import javax.transaction.Transactional;

@SpringBootTest
public class StartAnalysisTest {

    private static final String SCAN_ID_MOCK = "12345";

    private final StartAnalysis startAnalysis;
    private final AnalysisRepository analysisRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final TestUtils testUtils;

    private User user;
    private File file;
    private Analysis analysis;

    public StartAnalysisTest(
            @Autowired AnalysisJpaRepository analysisRepository,
            @Autowired UserJpaRepository userJpaRepository,
            @Autowired FileJpaRepository fileRepository,
            @Autowired TestUtils testUtils) {
        this.analysisRepository = new AnalysisJpaRepositoryAdapter(/*fileRepository, */analysisRepository /*, userJpaRepository*/);
        this.fileRepository = new FileJpaRepositoryAdapter(fileRepository/*, userJpaRepository*/);
        this.userRepository = new UserJpaRepositoryAdapter(userJpaRepository);
        this.startAnalysis = new StartAnalysis(new AnalysisAPIMock(), this.analysisRepository);
        this.testUtils = testUtils;
    }

    @BeforeEach
    public void prepare (){
        this.user = this.userRepository.save(testUtils.getRandomUser(false));
        this.file = this.fileRepository.save(testUtils.getRandomFile(this.user));
    }

    @Test
    public void execute_ShouldSaveAnalysis() {

        var analysis = new Analysis();
        analysis.setFile(file);
        analysis.setAnalysisStatus(AnalysisStatus.PENDING);
        var savedAnalysis = this.analysisRepository.save(analysis);

        Assertions.assertDoesNotThrow(() -> {
            this.startAnalysis.execute("path", savedAnalysis);
        });

        analysis = this.analysisRepository.findByScanId(SCAN_ID_MOCK).orElse(null);
        Assertions.assertNotNull(analysis);
        Assertions.assertEquals(savedAnalysis.getId(), analysis.getId());
        Assertions.assertEquals(AnalysisStatus.IN_PROGRESS, analysis.getAnalysisStatus());

        this.analysisRepository.deleteById(analysis.getId());
    }

    @AfterEach
    public void clean (){
        this.fileRepository.delete(this.file);
        this.userRepository.deleteById(this.user.getId());
    }

    static class AnalysisAPIMock implements AnalysisAPIHandler {

        @Override
        public String sendAnalysisRequest(String path) throws IOException {
            return SCAN_ID_MOCK; // ScanID
        }

        @Override
        public Analysis updateResult(Analysis analysis) throws JsonProcessingException {
            return null;
        }

    }
}