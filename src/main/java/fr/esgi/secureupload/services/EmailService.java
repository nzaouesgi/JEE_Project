package fr.esgi.secureupload.services;

import fr.esgi.secureupload.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender emailSender;

    @Autowired
    public EmailService (JavaMailSender emailSender){
        this.emailSender = emailSender;
    }

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendConfirmationMail(String mail, String confirmationLink){
        this.send(
                mail,
                "Please confirm your account",
                String.format("Use this link to confirm your account: %s", confirmationLink));
    }
}
