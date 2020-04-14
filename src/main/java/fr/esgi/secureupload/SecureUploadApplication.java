package fr.esgi.secureupload;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;
import fr.esgi.secureupload.utils.Crypto;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication

public class SecureUploadApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(SecureUploadApplication.class, args);

        UserService service = context.getBean(UserService.class);

        for (int i = 0; i < 100; i++){
            User user = User.builder()
                    .email(Crypto.randomString(6) + "user@domain.com")
                    .password("password")
                    .build();

            service.save(user);
        }

    }

}
