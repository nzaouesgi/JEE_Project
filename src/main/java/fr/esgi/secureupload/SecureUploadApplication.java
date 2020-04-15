package fr.esgi.secureupload;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class SecureUploadApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(SecureUploadApplication.class, args);


        UserService service = context.getBean(UserService.class);

        try {
            User user = User.builder().email("nzaou.renaud@live.fr").password("password").build();
            service.save(user);
        } catch (Exception e){

        }

    }

}
