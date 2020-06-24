package fr.esgi.secureupload;

import fr.esgi.secureupload.services.EmailService;
import fr.esgi.secureupload.utils.Utils;
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
    public void sendMail_ShouldSendMail () throws IOException, JSONException {

        // Send test mail to Mailhog SMTP
        String to = testUtils.getRandomMail();

        String subject = "Test mail";
        String body = "This is a test mail";

        Assertions.assertDoesNotThrow(() -> {
            this.emailService.send(to, subject, body);
        });

        JSONObject message = testUtils.getSentMail(to);

        Assertions.assertNotNull(message);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(to, headers.getJSONArray("To").get(0));
        Assertions.assertEquals(subject, headers.getJSONArray("Subject").get(0));
        Assertions.assertEquals(body, message.getString("Body"));
    }

    @Test
    public void sendConfirmationMail_ShouldSendConfirmationMail () throws IOException, JSONException {
        String to = testUtils.getRandomMail();
        String token = Utils.randomBytesToHex(8);

        Assertions.assertDoesNotThrow(() -> {
            this.emailService.sendConfirmationMail(to, "http://confirmhere.com/users/" + Utils.randomBytesToHex(8) + "/confirm?confirmationToken=" + token);
        });


        JSONObject message = testUtils.getSentMail(to);

        Assertions.assertNotNull(message);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(to, headers.getJSONArray("To").get(0));
        Assertions.assertTrue(message.getString("Body").contains(token));
    }

}
