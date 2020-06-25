package fr.esgi.secureupload.users.adapters.helpers;

import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class JavaMailConfirmationMailSender implements ConfirmationMailSender {

    private JavaMailSender emailSender;

    public JavaMailConfirmationMailSender(JavaMailSender emailSender){
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
    public void sendConfirmationMail(String mail, String confirmationLink){
        this.send(
                mail,
                "Please confirm your account",
                String.format("Use this link to confirm your account: %s", confirmationLink));
    }
}
