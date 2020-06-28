package fr.esgi.secureupload.users.ports;

public interface UserMailSender {
    void sendConfirmationMail(String email, String confirmationLink);
    void sendRecoveryMail(String email, String recoveryLink);
}
