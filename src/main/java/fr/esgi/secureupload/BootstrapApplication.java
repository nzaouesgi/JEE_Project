package fr.esgi.secureupload;

import fr.esgi.secureupload.common.adapters.helpers.SecureRandomTokenGenerator;
import fr.esgi.secureupload.users.adapters.helpers.SpringUserPasswordEncoder;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserRepositoryAdapter;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;


class BootstrapApplication {

    private UserRepositoryAdapter userJpaRepository;
    private RandomTokenGenerator tokenGenerator;
    private UserPasswordEncoder encoder;

    @Value("${secureupload.admin.email}")
    private String adminEmail;

    @Value("${secureupload.admin.password}")
    private String adminPassword;

    BootstrapApplication(@Autowired UserJpaRepository userJpaRepository, @Autowired PasswordEncoder springPasswordEncoder) {
        this.userJpaRepository = new UserRepositoryAdapter(userJpaRepository);
        this.tokenGenerator = new SecureRandomTokenGenerator();
        this.encoder = new SpringUserPasswordEncoder(springPasswordEncoder);
    }

    void createAdminUser(){
        User admin = User.builder()
                .email(this.adminEmail)
                .password(this.encoder.encode(this.adminPassword))
                .admin(true)
                .confirmed(true)
                .confirmationToken(tokenGenerator.generate(32))
                .build();
        this.userJpaRepository.save(admin);
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        this.createAdminUser();
    }
}
