package fr.esgi.secureupload;

import fr.esgi.secureupload.users.adapters.helpers.ConfirmationMailSenderImpl;
import fr.esgi.secureupload.common.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

@SpringBootTest
public class JavaMailConfirmationMailSenderTest {

    private ConfirmationMailSenderImpl sender;

    @Autowired
    private TestUtils testUtils;

    public JavaMailConfirmationMailSenderTest (@Autowired JavaMailSender javaMailSender){
        this.sender = new ConfirmationMailSenderImpl(javaMailSender);
    }

    @Test
    public void sendConfirmationMail_ShouldSendConfirmationMail () throws IOException, JSONException {
        String to = testUtils.getRandomMail();
        String token = Utils.randomBytesToHex(8);

        Assertions.assertDoesNotThrow(() -> {
            this.sender.sendConfirmationMail(to, "http://confirmhere.com/users/" + Utils.randomBytesToHex(8) + "/confirm?confirmationToken=" + token);
        });


        JSONObject message = testUtils.getSentMail(to);

        Assertions.assertNotNull(message);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(to, headers.getJSONArray("To").get(0));
        Assertions.assertTrue(message.getString("Body").contains(token));
    }

}
