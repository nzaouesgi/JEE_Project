package fr.esgi.secureupload.users;

import fr.esgi.secureupload.TestUtils;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SpringTestWithUsers {

    public static int USERS_COUNT = 100;
    public static int ADMINS_COUNT = 10;

    public static final List<User> users = new ArrayList<>();
    public static final List<User> admins = new ArrayList<>();

    public void deleteFromUsers(int i){
        users.remove(i);
        USERS_COUNT--;
    }

    public void deleteFromAdmin(int i){
        admins.remove(i);
        ADMINS_COUNT--;
    }

    @BeforeAll
    public static void prepareUsers (@Autowired UserJpaRepository jpaRepository, @Autowired TestUtils testUtils) {

        UserRepository repository = new UserJpaRepositoryAdapter(jpaRepository);

        for (int i = 0; i < USERS_COUNT; i++)
            SpringTestWithUsers.users.add(repository.save(testUtils.getRandomUser(false)));

        for (int i = 0; i < ADMINS_COUNT; i++)
            SpringTestWithUsers.admins.add(repository.save(testUtils.getRandomUser(true)));
    }

    @AfterAll
    public static void cleanUsers (@Autowired UserJpaRepository jpaRepository){

        UserRepository repository = new UserJpaRepositoryAdapter(jpaRepository);

        for (User user: users)
            repository.deleteById(user.getId());

        for (User admin: admins)
            repository.deleteById(admin.getId());

        users.clear();
        admins.clear();
    }


}
