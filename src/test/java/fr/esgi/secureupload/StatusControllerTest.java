package fr.esgi.secureupload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final static String STATUS_API_PATH = "/status";

    @Test
    public void testStatus_OK () throws Exception {
        this.mockMvc.perform(head(STATUS_API_PATH)).andExpect(status().isOk());
    }

}
