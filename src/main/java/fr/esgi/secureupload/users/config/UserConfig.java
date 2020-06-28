package fr.esgi.secureupload.users.config;

import fr.esgi.secureupload.common.adapters.helpers.SecureRandomTokenGenerator;
import fr.esgi.secureupload.users.adapters.helpers.UserFieldsValidatorImpl;
import fr.esgi.secureupload.users.adapters.helpers.UserPasswordEncoderImpl;
import fr.esgi.secureupload.users.adapters.helpers.ConfirmationMailSenderImpl;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.ports.ConfirmationMailSender;
import fr.esgi.secureupload.users.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.ports.UserFieldsValidator;
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

    private final UserRepository userJpaRepository;
    private final UserPasswordEncoder userPasswordEncoder;
    private final ConfirmationMailSender confirmationMailSender;
    private final RandomTokenGenerator randomTokenGenerator;
    private final UserFieldsValidator userFieldsValidator;

    public UserConfig(@Autowired UserJpaRepository userJpaRepository,
                      @Autowired JavaMailSender javaMailSender,
                      @Autowired PasswordEncoder passwordEncoder){

        this.userJpaRepository = new UserJpaRepositoryAdapter(userJpaRepository);
        this.userPasswordEncoder = new UserPasswordEncoderImpl(passwordEncoder);
        this.confirmationMailSender = new ConfirmationMailSenderImpl(javaMailSender);
        this.randomTokenGenerator = new SecureRandomTokenGenerator();
        this.userFieldsValidator = new UserFieldsValidatorImpl();
    }

    @Bean
    public ConfirmUser confirmUser(){
        return new ConfirmUser(this.userJpaRepository);
    }

    @Bean
    public CreateUser createUser() {
        return new CreateUser(
                this.userJpaRepository,
                this.userPasswordEncoder,
                this.confirmationMailSender,
                this.randomTokenGenerator,
                this.userFieldsValidator);
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
        return new ResetUserPassword(
                this.userJpaRepository,
                this.userPasswordEncoder,
                this.userFieldsValidator);
    }
}
