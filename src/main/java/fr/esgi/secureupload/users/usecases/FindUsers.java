package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class FindUsers {

    public final static int FIND_USERS_LIMIT = 1000;
    public final static String DEFAULT_ORDER_BY_FIELD = "email";

    private final UserRepository repository;

    public FindUsers(final UserRepository repository){
        this.repository = repository;
    }

    private void checkFields (String orderBy, int limit){

        for (String field : User.PRIVATE_FIELDS) {

            if (orderBy.equalsIgnoreCase(field)) {
                throw new UserSecurityException(String.format("Field %s is private.", orderBy));
            }

            if (limit > FIND_USERS_LIMIT)
                throw new UserSecurityException(String.format("\"limit\" parameter must not exceed %d", FIND_USERS_LIMIT));
        }
    }

    public Page<User> execute (int limit, int page, String orderBy, String orderMode, String pattern){

        Objects.requireNonNull(orderBy, "orderBy must not be null");
        Objects.requireNonNull(orderMode, "orderMode must not be null");

        this.checkFields(orderBy, limit);

        Sort sort = Sort.by(orderBy).ascending();
        if (orderMode.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }

        return pattern == null ?
                this.repository.findAll(PageRequest.of(page, limit, sort)) :
                this.repository.findAllByPattern(pattern, PageRequest.of(page, limit, sort));
    }

    public Page<User> execute (int limit, int page, String orderBy, String orderMode){
        return this.execute(limit, page, orderBy, orderMode, null);
    }

    public Page<User> execute (int limit, int page, String orderBy){
        return this.execute(limit, page, orderBy, "asc", null);
    }

    public Page<User> execute (int limit, int page){
        return this.execute(limit, page, DEFAULT_ORDER_BY_FIELD, "asc", null);
    }

    public Page<User> execute (int limit){
        return this.execute(limit, 0, DEFAULT_ORDER_BY_FIELD, "asc", null);
    }

    public Page<User> execute (){
        return this.execute(FIND_USERS_LIMIT, 0, DEFAULT_ORDER_BY_FIELD, "asc", null);
    }
}
