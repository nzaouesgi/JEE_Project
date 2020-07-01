package fr.esgi.secureupload.users.infrastructure.adapters.repositories;

import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
import java.util.Optional;


public final class UserJpaRepositoryAdapter implements UserRepository {

    private UserJpaRepository jpaRepository;

    public UserJpaRepositoryAdapter(UserJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<User> findAllByPattern(String pattern, Pageable pageable) {
        return this.jpaRepository.findAllByPattern(pattern, pageable)
                .map(UserJpaAdapter::convertToUser);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Page<UserJpaEntity> usersJpa = this.jpaRepository.findAll(pageable);
        return usersJpa.map(UserJpaAdapter::convertToUser);
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
