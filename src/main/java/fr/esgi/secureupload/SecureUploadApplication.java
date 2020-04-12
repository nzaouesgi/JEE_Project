package fr.esgi.secureupload;

import fr.esgi.secureupload.entities.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@SpringBootApplication
public class SecureUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureUploadApplication.class, args);


        EntityManagerFactory factory = Persistence.createEntityManagerFactory("MainStorage");
        EntityManager em = factory.createEntityManager();

        User user = new User();
        user.setEmail("nzaou.renaud@live.fr");
        user.setPassword("password");

        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();

    }

}
