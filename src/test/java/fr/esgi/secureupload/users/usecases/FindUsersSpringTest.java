package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Random;

@SpringBootTest
public class FindUsersSpringTest extends SpringTestWithUsers {

    private final FindUsers findUsers;

    public FindUsersSpringTest(@Autowired FindUsers findUsers) {
        this.findUsers = findUsers;
    }

    @Test
    public void execute_ShouldPaginateUsers (){

        int limit = USERS_COUNT / 2;

        Page<User> firstPage = findUsers.execute(limit, 0, "email", "asc");
        Assertions.assertNotNull(users);
        Assertions.assertEquals(limit, firstPage.getContent().size());

        Page<User> secondPage = findUsers.execute(limit, 1, "email", "asc");
        Assertions.assertNotNull(users);
        Assertions.assertEquals(limit, secondPage.getContent().size());

        for (int i = 0; i < limit; i ++){
            Assertions.assertNotEquals(firstPage.getContent().get(i).getId(), secondPage.getContent().get(i).getId());
        }

        Page<User> intermediatePage = findUsers.execute(limit / 2, 1, "email", "asc");
        for (int i = 0; i < limit / 2; i ++){
            Assertions.assertEquals(firstPage.getContent().get(i + (limit / 2)).getId(), intermediatePage.getContent().get(i).getId());
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
            findUsers.execute(USERS_COUNT, 0, User.PRIVATE_FIELDS[new Random().nextInt(User.PRIVATE_FIELDS.length)], "asc");
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
    public void execute_ShouldOrderByAnyFieldWithSpecifiedOrderMode (){

        for (String stringField: List.of("email", "id")) {

            Page<User> orderedDesc = findUsers.execute(10, 0, stringField, "desc");

            String last = null;
            for (User user : orderedDesc.getContent()) {
                String current = null;
                switch (stringField){
                    case "email":
                        current = user.getEmail();
                        break;
                    case "id":
                        current = user.getId();
                        break;
                }
                if (last != null) {
                    Assertions.assertTrue(current.compareTo(last) <= 0);
                }
                last = current;
            }
        }

        for (String stringField: List.of("email", "id")) {

            Page<User> orderedAsc = findUsers.execute(10, 0, stringField, "asc");

            String last = null;
            for (User user : orderedAsc.getContent()) {
                String current = null;
                switch (stringField){
                    case "email":
                        current = user.getEmail();
                        break;
                    case "id":
                        current = user.getId();
                        break;
                }
                if (last != null) {
                    Assertions.assertTrue(current.compareTo(last) >= 0);
                }
                last = current;
            }
        }

    }
}
