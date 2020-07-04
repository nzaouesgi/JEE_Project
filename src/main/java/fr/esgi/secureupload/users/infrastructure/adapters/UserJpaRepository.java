package fr.esgi.secureupload.users.infrastructure.adapters;

import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    @Query(value = "select u from User u where u.email like %:pattern% or u.id like %:pattern%")
    Page<UserJpaEntity> findAllByPattern(@Param(value="pattern") String pattern, Pageable pageable);

    Optional<UserJpaEntity> findByEmail(String email);
}
