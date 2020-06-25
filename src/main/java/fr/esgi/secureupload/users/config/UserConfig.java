package fr.esgi.secureupload.users.config;

import fr.esgi.secureupload.common.adapters.helpers.SecureRandomTokenGenerator;
import fr.esgi.secureupload.users.adapters.helpers.SpringUserPasswordEncoder;
import fr.esgi.secureupload.users.adapters.helpers.JavaMailConfirmationMailSender;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserRepositoryAdapter;
import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import fr.esgi.secureupload.users.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.repository.UserRepository;
import fr.esgi.secureupload.users.usecases.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {

    private UserRepository userJpaRepository;
    private UserPasswordEncoder userPasswordEncoder;
    private ConfirmationMailSender confirmationMailSender;
    private RandomTokenGenerator randomTokenGenerator;

    public UserConfig(@Autowired UserJpaRepository userJpaRepository,
                      @Autowired JavaMailSender javaMailSender,
                      @Autowired PasswordEncoder passwordEncoder){

        this.userJpaRepository = new UserRepositoryAdapter(userJpaRepository);
        this.userPasswordEncoder = new SpringUserPasswordEncoder(passwordEncoder);
        this.confirmationMailSender = new JavaMailConfirmationMailSender(javaMailSender);
        this.randomTokenGenerator = new SecureRandomTokenGenerator();
    }

    @Bean
    public ConfirmUser confirmUser(){
        return new ConfirmUser(this.userJpaRepository);
    }

    @Bean
    public CreateUser createUser() {
        return new CreateUser(this.userJpaRepository, this.userPasswordEncoder, this.confirmationMailSender, this.randomTokenGenerator);
    }

    @Bean
    public DeleteUser deleteUser(){
        return new DeleteUser(this.userJpaRepository);
    }

    @Bean
    public FindUserByEmail findUserByEmail (){
        return new FindUserByEmail(this.userJpaRepository);
    }

    @Bean
    public FindUserById findUserById (){
        return new FindUserById(this.userJpaRepository);
    }

    @Bean
    public FindUsers findUsers (){
        return new FindUsers(this.userJpaRepository);
    }

    @Bean
    public ResetUserPassword resetUserPassword (){
        return new ResetUserPassword(this.userJpaRepository, this.userPasswordEncoder);
    }
}
