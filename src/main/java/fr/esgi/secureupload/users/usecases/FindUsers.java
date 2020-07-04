package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.entities.UserOrderByField;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.domain.repository.UserRepository;

import java.util.Objects;

public final class FindUsers {

    public final static int FIND_USERS_LIMIT = 1000;
    public final static UserOrderByField DEFAULT_ORDER_BY_FIELD = UserOrderByField.EMAIL;

    private final UserRepository repository;

    public FindUsers(final UserRepository repository){
        this.repository = repository;
    }

    public EntitiesPage<User> execute (int limit, int page, UserOrderByField orderBy, OrderMode orderMode, String pattern){

        Objects.requireNonNull(orderBy, "orderBy must not be null");
        Objects.requireNonNull(orderMode, "orderMode must not be null");

        if (limit > FIND_USERS_LIMIT)
            throw new UserSecurityException(String.format("\"limit\" parameter must not exceed %d", FIND_USERS_LIMIT));

        return pattern == null ?
                this.repository.findAll(limit, page, orderBy, orderMode) :
                this.repository.findAllByPattern(limit, page, orderBy, orderMode, pattern);
    }

    public EntitiesPage<User> execute (int limit, int page, UserOrderByField orderBy, OrderMode orderMode){
        return this.execute(limit, page, orderBy, orderMode, null);
    }

    public EntitiesPage<User> execute (int limit, int page, UserOrderByField orderBy){
        return this.execute(limit, page, orderBy, OrderMode.ASC, null);
    }

    public EntitiesPage<User> execute (int limit, int page){
        return this.execute(limit, page, DEFAULT_ORDER_BY_FIELD, OrderMode.ASC, null);
    }

    public EntitiesPage<User> execute (int limit){
        return this.execute(limit, 0, DEFAULT_ORDER_BY_FIELD, OrderMode.ASC, null);
    }

    public EntitiesPage<User> execute (){
        return this.execute(FIND_USERS_LIMIT, 0, DEFAULT_ORDER_BY_FIELD, OrderMode.ASC, null);
    }
}
