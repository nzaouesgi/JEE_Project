package fr.esgi.secureupload.users.domain.ports;

public interface UserMailSender {
    void sendConfirmationMail(String email, String confirmationLink);
    void sendRecoveryMail(String email, String recoveryLink);
}
