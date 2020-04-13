package fr.esgi.secureupload.repositories;

import fr.esgi.secureupload.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Search users by string pattern.
     * @param pattern This is the search pattern to use, it will be applied to all string fields in the entity.
     * @param pageable A pageable supplying which page to access and limit of results per page, typically a PageRequest
     * @return a Page<User> containing all matching records
     */
    @Query(value = "select u from User u where u.email like %:pattern% or u.uuid like %:pattern%")
    Page<User> findAllByPattern(@Param(value="pattern") String pattern, Pageable pageable);

    User findByEmail(String email);
}
