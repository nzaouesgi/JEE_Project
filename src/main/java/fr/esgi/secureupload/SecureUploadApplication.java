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

        ApplicationContext context =  SpringApplication.run(SecureUploadApplication.class, args);

        User user = User.builder()
            .email(Crypto.randomString(5) + "nzaou.renaud@live.fr")
            .password("password")
            .isAdmin(false)
            .build();

        UserService userService = context.getBean(UserService.class);
        User saved = userService.save(user);

        System.out.println("looking for " + saved.getUuid().length() + " ok");
        System.out.println(userService.findById(saved.getUuid()));
    }

}
