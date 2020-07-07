package fr.esgi.secureupload.users.infrastructure.adapters;

import fr.esgi.secureupload.users.domain.entities.User;
import org.apache.commons.beanutils.BeanUtils;

public class UserJpaAdapter {

    private final UserJpaRepository userJpaRepository;

    public UserJpaAdapter (UserJpaRepository userJpaRepository){
        this.userJpaRepository = userJpaRepository;
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
        UserJpaEntity userJpa;

        if (user.getId() == null){
            userJpa = new UserJpaEntity();
        } else {
            userJpa = this.userJpaRepository.findById(user.getId()).orElseThrow();
        }

        userJpa.setCreatedAt(user.getCreatedAt());
        userJpa.setUpdatedAt(user.getUpdatedAt());
        userJpa.setEmail(user.getEmail());
        userJpa.setPassword(user.getPassword());
        userJpa.setAdmin(user.isAdmin());
        userJpa.setConfirmed(user.isConfirmed());
        userJpa.setConfirmationToken(user.getConfirmationToken());
        userJpa.setRecoveryToken(user.getRecoveryToken());

        return userJpa;
    }
}
