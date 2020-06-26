package fr.esgi.secureupload.users.adapters.helpers;

import fr.esgi.secureupload.TestUtils;
import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Objects;

@SpringBootTest

public class ConfirmationMailSenderImplTest {

    private ConfirmationMailSender sender;

    @Autowired
    private TestUtils testUtils;

    private static final String to = "test@domain.fr";
    private static final String token = "0f442720-67b6-4790-8b5d-913acd7e42c3";

    public ConfirmationMailSenderImplTest(@Autowired JavaMailSender javaMailSender){
        this.sender = new ConfirmationMailSenderImpl(javaMailSender);
    }

    @Test
    public void sendConfirmationMail_ShouldSendConfirmationMail () throws IOException, JSONException {

        String to = testUtils.getRandomMail();
        String token = "randomtoken";

        Assertions.assertDoesNotThrow(() -> {
            this.sender.sendConfirmationMail(to, "http://confirmhere.com/users/someid/confirm?confirmationToken=" + token);
        });

        JSONObject message = testUtils.getSentMail(to);

        Assertions.assertNotNull(message);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(to, headers.getJSONArray("To").get(0));
        Assertions.assertTrue(message.getString("Body").contains(token));
    }

}
