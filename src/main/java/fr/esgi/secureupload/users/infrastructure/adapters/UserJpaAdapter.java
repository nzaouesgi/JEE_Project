package fr.esgi.secureupload.users.infrastructure.adapters;

import fr.esgi.secureupload.users.domain.entities.User;
import org.apache.commons.beanutils.BeanUtils;

public class UserJpaAdapter {

    public static User convertToUser (final UserJpaEntity userJpa){
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

    public static UserJpaEntity convertToJpaEntity (final User user) {
        UserJpaEntity userJpa = new UserJpaEntity();
        userJpa.setId(user.getId());
        userJpa.setCreatedAt(user.getCreatedAt());
        userJpa.setUpdatedAt(user.getUpdatedAt());
        userJpa.setEmail(user.getEmail());
        userJpa.setAdmin(user.isAdmin());
        userJpa.setConfirmed(user.isConfirmed());
        userJpa.setConfirmationToken(user.getConfirmationToken());
        userJpa.setRecoveryToken(user.getRecoveryToken());
        return userJpa;
    }
}
