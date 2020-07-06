package fr.esgi.secureupload.users;

import fr.esgi.secureupload.utils.TestUtils;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class SpringTestWithUsers {

    public static final List<User> users = new ArrayList<>();
    public static final List<User> admins = new ArrayList<>();

    @BeforeAll
    public static void prepare (@Autowired UserJpaRepository jpaRepository, @Autowired TestUtils testUtils){
        prepareUsers(jpaRepository, testUtils);
    }

    public static void prepareUsers (UserJpaRepository jpaRepository, TestUtils testUtils) {

        UserRepository repository = new UserJpaRepositoryAdapter(jpaRepository);

        int USERS_COUNT = 100;
        for (int i = 0; i < USERS_COUNT; i++)
            SpringTestWithUsers.users.add(repository.save(testUtils.getRandomUser(false)));

        int ADMINS_COUNT = 10;
        for (int i = 0; i < ADMINS_COUNT; i++)
            SpringTestWithUsers.admins.add(repository.save(testUtils.getRandomUser(true)));
    }

    public static User randomUser(){
        return users.get(new Random().nextInt(users.size()));
    }

    @AfterAll
    public static void clean (@Autowired UserJpaRepository jpaRepository){
        cleanUsers(jpaRepository);
    }

    public static void cleanUsers (@Autowired UserJpaRepository jpaRepository){
        jpaRepository.deleteAll();
        users.clear();
        admins.clear();
    }
}
