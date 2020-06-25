package fr.esgi.secureupload;

import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserRepositoryAdapter;
import fr.esgi.secureupload.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


class BootstrapApplication {

    private UserRepositoryAdapter userJpaRepositoryImpl;

    @Value("${secureupload.admin.email}")
    private String adminEmail;

    @Value("${secureupload.admin.password}")
    private String adminPassword;

    BootstrapApplication(@Autowired UserJpaRepository userJpaRepository) {
        this.userJpaRepositoryImpl = new UserRepositoryAdapter(userJpaRepository);
    }

    void createAdminUser(){
        User admin = User.builder()
                .email(this.adminEmail)
                .password(this.adminPassword)
                .admin(true)
                .confirmed(true)
                .build();
        this.userJpaRepositoryImpl.save(admin);
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        this.createAdminUser();
    }
}
