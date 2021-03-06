package fr.esgi.secureupload.users.infrastructure.adapters.helpers;

import fr.esgi.secureupload.utils.TestUtils;
import fr.esgi.secureupload.users.domain.ports.UserMailSender;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

@SpringBootTest

public class MailSenderImplTest {

    private UserMailSender sender;

    @Autowired
    private TestUtils testUtils;

    public MailSenderImplTest(@Autowired JavaMailSender javaMailSender){
        this.sender = new MailSenderImpl(javaMailSender);
    }

    @Test
    public void sendConfirmationMail_ShouldSendConfirmationMail () throws IOException, JSONException {

        String to = testUtils.getRandomMail();
        String token = "randomtoken";

        Assertions.assertDoesNotThrow(() -> {
            this.sender.sendConfirmationMail(to, "http://secureupload.com/users/someid/confirm?confirmationToken=" + token);
        });

        JSONObject message = testUtils.getSentMail(to);

        Assertions.assertNotNull(message);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(to, headers.getJSONArray("To").get(0));
        Assertions.assertTrue(message.getString("Body").contains(token));
    }

}
