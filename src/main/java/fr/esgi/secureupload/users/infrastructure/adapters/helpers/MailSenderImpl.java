package fr.esgi.secureupload.users.infrastructure.adapters.helpers;

import fr.esgi.secureupload.users.domain.ports.UserMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailSenderImpl implements UserMailSender {

    private JavaMailSender emailSender;

    public MailSenderImpl(JavaMailSender emailSender){
        this.emailSender = emailSender;
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendConfirmationMail(String email, String confirmationLink){
        this.send(
                email,
                "Please confirm your account",
                String.format("Use this link to confirm your account: %s", confirmationLink));
    }

    @Override
    public void sendRecoveryMail(String email, String recoveryLink) {
        this.send(
                email,
                "Here is your recovery token",
                String.format("Use this link to recover your password: %s", recoveryLink));
    }
}
