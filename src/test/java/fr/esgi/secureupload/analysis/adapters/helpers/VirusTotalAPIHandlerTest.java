package fr.esgi.secureupload.analysis.adapters.helpers;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.analysis.infrastructure.adapters.helpers.VirusTotalAPIHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
public class VirusTotalAPIHandlerTest {

    @Autowired
    VirusTotalAPIHandler apiHandler;

    @Test
    public String testAnalysisRequest() throws IOException {
        Path directory = Files.createTempDirectory(null);
        File file = new File(directory.toString() + "/test2.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("This is a test tregrefgdgfd");
        String result = apiHandler.sendAnalysisRequest(file.getPath());
        return result;
    }

    @Test
    public void testAnalysisResult() throws IOException {
        Analysis analysis = new Analysis();
        String result = testAnalysisRequest();
        analysis.setScanId(result);
        analysis = apiHandler.getAnalysisResult(analysis);
        System.out.println(analysis.getTotalScans());
    }

}
