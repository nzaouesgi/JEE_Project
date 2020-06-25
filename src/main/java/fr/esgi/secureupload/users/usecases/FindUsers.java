package fr.esgi.secureupload.users.usecases;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class FindUsers {

    public final static int FIND_USERS_LIMIT = 100;

    private final UserRepository repository;

    public FindUsers(final UserRepository repository){
        this.repository = repository;
    }

    public Page<User> execute (int limit, int page, String orderBy, String orderMode, String pattern){

        for (String field : User.PRIVATE_FIELDS) {
            if (orderBy.equalsIgnoreCase(field)) {
                throw new UserSecurityException(String.format("Field %s is private.", orderBy));
            }
        }

        if (limit > FIND_USERS_LIMIT)
            throw new UserSecurityException(String.format("\"limit\" parameter must not exceed %d", FIND_USERS_LIMIT));

        Sort sort = Sort.by(orderBy);
        if (orderMode.equalsIgnoreCase("desc"))
            sort.descending();

        return pattern == null ?
                this.repository.findAll(PageRequest.of(page, limit, sort)) :
                this.repository.findAllByPattern(pattern, PageRequest.of(page, limit, sort));
    }

    public Page<User> execute (int limit, int page, String orderBy, String orderMode){
        return this.execute(limit, page, orderBy, orderMode, null);
    }
}
