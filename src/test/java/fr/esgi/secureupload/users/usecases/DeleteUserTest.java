package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeleteUserTest {

    private UserRepository repository;

    public DeleteUserTest (@Autowired UserJpaRepository jpaRepository){
        this.repository = new UserJpaRepositoryAdapter(jpaRepository);
    }

    @Test
    public void execute_ShouldDeleteUser (){

    }
}
