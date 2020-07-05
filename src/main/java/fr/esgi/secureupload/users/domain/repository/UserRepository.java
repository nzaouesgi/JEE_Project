package fr.esgi.secureupload.users.domain.repository;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.entities.UserOrderByField;

import java.util.Optional;

public interface UserRepository {

    EntitiesPage<User> findAllByPattern(int limit, int page, UserOrderByField orderBy, OrderMode orderMode, String pattern);

    EntitiesPage<User> findAll(int limit, int page, UserOrderByField orderBy, OrderMode orderMode);

    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    User save (User user);

    void deleteById (String id);

    User getOne (String id);
}
