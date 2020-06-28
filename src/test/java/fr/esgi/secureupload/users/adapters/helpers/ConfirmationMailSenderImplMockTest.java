package fr.esgi.secureupload.users.adapters.helpers;

import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ConfirmationMailSenderImplMockTest {

    @Mock
    JavaMailSender mockJavaMailSender;

    private static final String to = "test@domain.fr";
    private static final String token = "0f442720-67b6-4790-8b5d-913acd7e42c3";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendConfirmationMail_ShouldCallSendFromJavaMail (){

        ConfirmationMailSenderImpl sender = new ConfirmationMailSenderImpl(this.mockJavaMailSender);

        sender.sendConfirmationMail(to, token);

        verify(this.mockJavaMailSender).send(argThat(new ConfirmationMailChecker()));
    }

    private static final class ConfirmationMailChecker implements ArgumentMatcher<SimpleMailMessage> {

        @Override
        public boolean matches(SimpleMailMessage simpleMailMessage) {

            if (Objects.requireNonNull(simpleMailMessage.getTo()).length < 1)
                return false;

            if (!simpleMailMessage.getTo()[0].equals(ConfirmationMailSenderImplMockTest.to))
                return false;

            return Objects.requireNonNull(simpleMailMessage.getText()).contains(token);
        }
    }
}
