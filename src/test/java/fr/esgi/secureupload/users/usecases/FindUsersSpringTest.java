package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.entities.UserOrderByField;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;

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

        int limit = users.size() / 2;

        EntitiesPage<User> firstPage = findUsers.execute(limit, 0, UserOrderByField.EMAIL, OrderMode.ASC);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(limit, firstPage.getContent().size());

        EntitiesPage<User> secondPage = findUsers.execute(limit, 1, UserOrderByField.EMAIL, OrderMode.ASC);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(limit, secondPage.getContent().size());

        for (int i = 0; i < limit; i ++){
            Assertions.assertNotEquals(firstPage.getContent().get(i).getId(), secondPage.getContent().get(i).getId());
        }

        EntitiesPage<User> intermediatePage = findUsers.execute(limit / 2, 1, UserOrderByField.EMAIL, OrderMode.ASC);
        for (int i = 0; i < limit / 2; i ++){
            Assertions.assertEquals(firstPage.getContent().get(i + (limit / 2)).getId(), intermediatePage.getContent().get(i).getId());
        }
    }

    @Test
    public void execute_ShouldThrowWhenMaxLimitIsReached (){
        Assertions.assertThrows(UserSecurityException.class, () -> {
           findUsers.execute(FindUsers.FIND_USERS_LIMIT + 1, 0, UserOrderByField.EMAIL, OrderMode.ASC);
        });
    }

    @Test
    public void execute_ShouldSearchByPattern (){

        EntitiesPage<User> foundByFullMail = findUsers.execute(1, 0, UserOrderByField.EMAIL, OrderMode.ASC, users.get(0).getEmail());
        Assertions.assertEquals(1, foundByFullMail.getContent().size());

        EntitiesPage<User> foundBySliceOfMail = findUsers.execute(1, 0, UserOrderByField.EMAIL, OrderMode.ASC, users.get(0).getEmail().substring(0, 6));
        Assertions.assertEquals(1, foundBySliceOfMail.getContent().size());

        EntitiesPage<User> foundByFullId = findUsers.execute(1, 0, UserOrderByField.EMAIL, OrderMode.ASC, users.get(0).getId());
        Assertions.assertEquals(1, foundByFullId.getContent().size());

        EntitiesPage<User> foundBySliceOfId = findUsers.execute(1, 0, UserOrderByField.EMAIL, OrderMode.ASC, users.get(0).getId().substring(0, 6));
        Assertions.assertEquals(1, foundBySliceOfId.getContent().size());
    }

    @Test
    public void execute_ShouldOrderByAnyFieldWithSpecifiedOrderMode (){

        for (UserOrderByField field: List.of(UserOrderByField.EMAIL, UserOrderByField.ID)) {

            EntitiesPage<User> orderedDesc = findUsers.execute(10, 0, field, OrderMode.DESC);

            String last = null;
            for (User user : orderedDesc.getContent()) {
                String current = null;
                switch (field){
                    case EMAIL:
                        current = user.getEmail();
                        break;
                    case ID:
                        current = user.getId();
                        break;
                }
                if (last != null) {
                    Assertions.assertTrue(current.compareTo(last) <= 0);
                }
                last = current;
            }
        }

        for (UserOrderByField field: List.of(UserOrderByField.EMAIL, UserOrderByField.ID)) {

            EntitiesPage<User> orderedAsc = findUsers.execute(10, 0, field, OrderMode.ASC);

            String last = null;
            for (User user : orderedAsc.getContent()) {
                String current = null;
                switch (field){
                    case EMAIL:
                        current = user.getEmail();
                        break;
                    case ID:
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
