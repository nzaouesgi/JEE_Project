package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.TestUtils;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class FindUsersTest {

    private static final int USERS_COUNT = 200;
    private final FindUsers findUsers;

    private static final List<User> users = new ArrayList<>();

    public FindUsersTest(@Autowired FindUsers findUsers) {
        this.findUsers = findUsers;
    }

    @BeforeAll
    public static void prepareUsers (@Autowired UserJpaRepository jpaRepository, @Autowired TestUtils testUtils){
        UserRepository repository = new UserJpaRepositoryAdapter(jpaRepository);
        for (int i = 0; i < USERS_COUNT; i ++)
            FindUsersTest.users.add(repository.save(testUtils.getRandomUser(false)));
    }

    @Test
    public void execute_ShouldPaginateUsers (){

        Page<User> firstPage = findUsers.execute(50, 0, "email", "asc");
        Assertions.assertNotNull(users);
        Assertions.assertEquals(50, firstPage.getContent().size());

        Page<User> secondPage = findUsers.execute(50, 1, "email", "asc");
        Assertions.assertNotNull(users);
        Assertions.assertEquals(50, secondPage.getContent().size());

        for (int i = 0; i < 50; i ++){
            Assertions.assertNotEquals(firstPage.getContent().get(i).getId(), secondPage.getContent().get(i).getId());
        }

        Page<User> intermediatePage = findUsers.execute(25, 1, "email", "asc");
        for (int i = 0; i < 25; i ++){
            Assertions.assertEquals(firstPage.getContent().get(i + 25).getId(), intermediatePage.getContent().get(i).getId());
        }
    }

    @Test
    public void execute_ShouldThrowWhenMaxLimitIsReached (){
        Assertions.assertThrows(UserSecurityException.class, () -> {
           findUsers.execute(FindUsers.FIND_USERS_LIMIT + 1, 0, "email", "asc");
        });
    }

    @Test
    public void execute_ShouldThrowWhenPrivateFieldInOrderBy (){
        Assertions.assertThrows(UserSecurityException.class, () -> {
            findUsers.execute(50, 0, User.PRIVATE_FIELDS[new Random().nextInt(User.PRIVATE_FIELDS.length)], "asc");
        });
    }

    @Test
    public void execute_ShouldSearchByPattern (){

        Page<User> foundByFullMail = findUsers.execute(1, 0, "email", "asc", users.get(0).getEmail());
        Assertions.assertEquals(1, foundByFullMail.getContent().size());

        Page<User> foundBySliceOfMail = findUsers.execute(1, 0, "email", "asc", users.get(0).getEmail().substring(0, 6));
        Assertions.assertEquals(1, foundBySliceOfMail.getContent().size());

        Page<User> foundByFullId = findUsers.execute(1, 0, "email", "asc", users.get(0).getId());
        Assertions.assertEquals(1, foundByFullId.getContent().size());

        Page<User> foundBySliceOfId = findUsers.execute(1, 0, "email", "asc", users.get(0).getId().substring(0, 6));
        Assertions.assertEquals(1, foundBySliceOfId.getContent().size());
    }

    @Test
    public void execute_ShouldOrderByAnyFieldWithSpecifedOrderMode (){

        for (String stringField: List.of("email", "id")) {

            Page<User> orderedDesc = findUsers.execute(1, 0, stringField, "desc");

            String current = null;
            for (User user : orderedDesc.getContent()) {
                String val = null;
                switch (stringField){
                    case "email":
                        val = user.getEmail();
                        break;
                    case "id":
                        val = user.getId();
                        break;
                }
                if (current != null) {
                    Assertions.assertTrue(val.compareTo(current) > 0);
                }
                current = val;
            }
        }

        for (String stringField: List.of("email", "id")) {

            Page<User> orderedDesc = findUsers.execute(1, 0, stringField, "asc");

            String current = null;
            for (User user : orderedDesc.getContent()) {
                String val = null;
                switch (stringField){
                    case "email":
                        val = user.getEmail();
                        break;
                    case "id":
                        val = user.getId();
                        break;
                }
                if (current != null) {
                    Assertions.assertTrue(val.compareTo(current) < 0);
                }
                current = val;
            }
        }
    }

    @AfterAll
    public static void cleanUsers (@Autowired UserJpaRepository jpaRepository){
        UserRepository repository = new UserJpaRepositoryAdapter(jpaRepository);
        for (User user: users)
            repository.delete(user);
    }
}
