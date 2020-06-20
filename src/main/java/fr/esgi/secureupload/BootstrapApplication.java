package fr.esgi.secureupload;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class BootstrapApplication {

    private final UserService userService;

    @Value("${secureupload.admin.email}")
    private String adminEmail;

    @Value("${secureupload.admin.password}")
    private String adminPassword;

    BootstrapApplication(UserService userService) {
        this.userService = userService;
    }

    void createAdminUser(){
        User admin = User.builder()
                .email(this.adminEmail)
                .password(this.adminPassword)
                .isAdmin(true)
                .isConfirmed(true)
                .build();
        this.userService.save(admin);
    }

    @EventListener
    void onStartup(ApplicationReadyEvent event) {
        this.createAdminUser();
    }
}
