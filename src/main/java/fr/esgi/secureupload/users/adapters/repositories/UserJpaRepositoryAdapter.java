package fr.esgi.secureupload.users.adapters.repositories;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
import java.util.Optional;


public final class UserJpaRepositoryAdapter implements UserRepository {

    private UserJpaRepository jpaRepository;

    public UserJpaRepositoryAdapter(UserJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    public User convertToUser (final UserJpaEntity userJpa){

        return User.builder()
                .id(userJpa.getId())
                .createdAt(userJpa.getCreatedAt())
                .updatedAt(userJpa.getUpdatedAt())
                .email(userJpa.getEmail())
                .password(userJpa.getPassword())
                .admin(userJpa.isAdmin())
                .confirmed(userJpa.isConfirmed())
                .confirmationToken(userJpa.getConfirmationToken())
                .recoveryToken(userJpa.getRecoveryToken())
                .build();
    }

    public UserJpaEntity convertToJpaEntity (final User user) {
        UserJpaEntity userJpa = new UserJpaEntity();
        try {
            BeanUtils.copyProperties(userJpa, user);
        } catch (Exception e){
            return null;
        }
        return userJpa;
    }

    @Override
    public Page<User> findAllByPattern(String pattern, Pageable pageable) {
        return this.jpaRepository.findAllByPattern(pattern, pageable)
                .map(this::convertToUser);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Page<UserJpaEntity> usersJpa = this.jpaRepository.findAll(pageable);
        return usersJpa.map(this::convertToUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.jpaRepository.findByEmail(email)
                .map(this::convertToUser);
    }

    @Override
    public Optional<User> findById(String id) {
        return this.jpaRepository.findById(id)
                .map(this::convertToUser);
    }

    @Override
    public User save(User user)  {
        return this.convertToUser(this.jpaRepository.save(Objects.requireNonNull(this.convertToJpaEntity(user))));
    }

    @Override
    public void delete(User user) {
        this.jpaRepository.delete(Objects.requireNonNull(this.convertToJpaEntity(user)));
    }
}
