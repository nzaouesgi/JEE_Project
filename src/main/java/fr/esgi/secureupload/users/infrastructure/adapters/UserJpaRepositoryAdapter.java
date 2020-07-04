package fr.esgi.secureupload.users.infrastructure.adapters;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.common.infrastructure.adapters.PageAdapter;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.entities.UserOrderByField;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.Optional;


public final class UserJpaRepositoryAdapter implements UserRepository {

    private UserJpaRepository jpaRepository;

    public UserJpaRepositoryAdapter(UserJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EntitiesPage<User> findAllByPattern(int limit, int page, UserOrderByField orderBy, OrderMode orderMode, String pattern) {

        Sort sort = Sort.by(orderBy.name().toLowerCase()).ascending();
        if (orderMode == OrderMode.DESC)
            sort = sort.descending();

        Page<User> springPage = this.jpaRepository.findAllByPattern(pattern, PageRequest.of(page, limit, sort))
                .map(UserJpaAdapter::convertToUser);

        return PageAdapter.convertToEntitiesPage(springPage);
    }

    @Override
    public EntitiesPage<User> findAll(int limit, int page, UserOrderByField orderBy, OrderMode orderMode) {

        Sort sort = Sort.by(orderBy.name().toLowerCase()).ascending();
        if (orderMode == OrderMode.DESC)
            sort = sort.descending();

        Page<User> springPage = this.jpaRepository.findAll(PageRequest.of(page, limit, sort))
                .map(UserJpaAdapter::convertToUser);

        return PageAdapter.convertToEntitiesPage(springPage);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.jpaRepository.findByEmail(email)
                .map(UserJpaAdapter::convertToUser);
    }

    @Override
    public Optional<User> findById(String id) {
        return this.jpaRepository.findById(id)
                .map(UserJpaAdapter::convertToUser);
    }

    @Override
    public User save(User user)  {
        return UserJpaAdapter.convertToUser(this.jpaRepository.save(Objects.requireNonNull(UserJpaAdapter.convertToJpaEntity(user))));
    }

    @Override
    public void deleteById(String id) {
        this.jpaRepository.deleteById(id);
    }
}
