package fr.esgi.secureupload;

import fr.esgi.secureupload.services.EmailService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TestUtils testUtils;

    @Test
    public void sendMail () throws IOException, JSONException {

        // Send test mail to Mailhog SMTP
        String to = testUtils.getRandomMail();

        String subject = "Test mail";
        String body = "This is a test mail";

        System.out.println(to);

        Assertions.assertDoesNotThrow(() -> {
            this.emailService.send(to, subject, body);
        });

        // Read sent message for Mailhog API.
        JSONObject message = testUtils.getSentMail(to);

        Assertions.assertNotNull(message);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(to, headers.getJSONArray("To").get(0));
        Assertions.assertEquals(subject, headers.getJSONArray("Subject").get(0));
        Assertions.assertEquals(body, message.getString("Body"));
    }

}
