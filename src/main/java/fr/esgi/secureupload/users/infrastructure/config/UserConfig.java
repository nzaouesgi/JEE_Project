package fr.esgi.secureupload.users.infrastructure.config;

import fr.esgi.secureupload.common.infrastructure.adapters.helpers.SecureRandomTokenGenerator;
import fr.esgi.secureupload.users.infrastructure.adapters.helpers.UserFieldsValidatorImpl;
import fr.esgi.secureupload.users.infrastructure.adapters.helpers.UserPasswordEncoderImpl;
import fr.esgi.secureupload.users.infrastructure.adapters.helpers.MailSenderImpl;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.domain.ports.UserMailSender;
import fr.esgi.secureupload.common.domain.ports.RandomTokenGenerator;
import fr.esgi.secureupload.users.domain.ports.UserFieldsValidator;
import fr.esgi.secureupload.users.domain.ports.UserPasswordEncoder;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
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
    private final UserMailSender userMailSender;
    private final RandomTokenGenerator randomTokenGenerator;
    private final UserFieldsValidator userFieldsValidator;

    public UserConfig(@Autowired UserJpaRepository userJpaRepository,
                      @Autowired JavaMailSender javaMailSender,
                      @Autowired PasswordEncoder passwordEncoder){

        this.userJpaRepository = new UserJpaRepositoryAdapter(userJpaRepository);
        this.userPasswordEncoder = new UserPasswordEncoderImpl(passwordEncoder);
        this.userMailSender = new MailSenderImpl(javaMailSender);
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
                this.userMailSender,
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

    @Bean
    public CreateRecoveryToken createRecoveryToken (){
        return new CreateRecoveryToken(
                this.userJpaRepository, this.randomTokenGenerator, this.userMailSender);
    }
}
