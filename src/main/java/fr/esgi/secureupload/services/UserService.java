package fr.esgi.secureupload.services;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findById(String uuid){
        return this.userRepository.findById(uuid)
                .orElse(null);
    }

    public User save(User user){
        if (user == null)
            throw new NullPointerException("Provided user is null.");
        return this.userRepository.save(user);
    }

    public void delete(User user) {
        if (user == null)
            throw new NullPointerException("Provided user is null.");
        this.userRepository.delete(user);
    }

    public Page<User> findAllByPattern(String pattern, int page, int size, Sort sort){
        if (pattern == null)
            throw new NullPointerException("Provided pattern is null");
        return this.userRepository.findAllByPattern(pattern, PageRequest.of(page, size, sort));
    }

    public Page<User> findAll (int page, int size, Sort sort){
        return this.userRepository.findAll(PageRequest.of(page, size, sort));
    }

    public User findByEmail (String email){
        return this.userRepository.findByEmail(email);
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.findByEmail(email);

        if (user == null || !user.isConfirmed())
            throw new UsernameNotFoundException(String.format("User with mail %s could not be found, or is not confirmed", email));

        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(user.getPassword())
                .roles(user.isAdmin() ? "ADMIN" : "USER")
                .build();
    }

}
