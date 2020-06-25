package fr.esgi.secureupload.users.ports;

public interface ConfirmationMailSender {
    void sendConfirmationMail(String mail, String confirmationLink);
}
