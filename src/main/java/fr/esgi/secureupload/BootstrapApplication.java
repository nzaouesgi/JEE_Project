package fr.esgi.secureupload;

import fr.esgi.secureupload.common.infrastructure.adapters.helpers.SecureRandomTokenGenerator;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.helpers.UserPasswordEncoderImpl;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.common.domain.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.domain.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.infrastructure.controllers.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class BootstrapApplication {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserRepository repository;
    private RandomTokenGenerator tokenGenerator;
    private UserPasswordEncoder encoder;

    @Value("${secureupload.admin.email}")
    private String adminEmail;

    @Value("${secureupload.admin.password}")
    private String adminPassword;

    BootstrapApplication(@Autowired UserJpaRepository userJpaRepository, @Autowired PasswordEncoder springPasswordEncoder) {
        this.repository = new UserJpaRepositoryAdapter(userJpaRepository);
        this.tokenGenerator = new SecureRandomTokenGenerator();
        this.encoder = new UserPasswordEncoderImpl(springPasswordEncoder);
    }

    void createAdminUser(){

        Optional<User> user = this.repository.findByEmail(this.adminEmail);
        if (user.isPresent())
            return;

        User admin = User.builder()
                .email(this.adminEmail)
                .password(this.encoder.encode(this.adminPassword))
                .admin(true)
                .confirmed(true)
                .confirmationToken(tokenGenerator.generate(32))
                .build();

        this.repository.save(admin);
        logger.info("Created admin user " + admin.getEmail());
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        this.createAdminUser();
    }
}
