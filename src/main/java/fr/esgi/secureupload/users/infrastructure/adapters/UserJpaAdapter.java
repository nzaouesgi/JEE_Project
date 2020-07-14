package fr.esgi.secureupload.users.infrastructure.adapters;

import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaEntity;
import fr.esgi.secureupload.users.domain.entities.User;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserJpaAdapter {

    private static final Map<String, UserJpaEntity> cachedEntities = new ConcurrentHashMap<>();

    //private final UserJpaRepository userJpaRepository;

    public UserJpaAdapter (/*UserJpaRepository userJpaRepository*/){
        //this.userJpaRepository = userJpaRepository;
    }

    public User convertToUser (final UserJpaEntity userJpa){

        cachedEntities.remove(userJpa.getId());
        cachedEntities.put(userJpa.getId(), userJpa);

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
            userJpa = cachedEntities.get(user.getId());
            //userJpa = this.userJpaRepository.findById(user.getId()).orElseThrow();
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
