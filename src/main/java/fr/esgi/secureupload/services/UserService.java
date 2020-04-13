package fr.esgi.secureupload.services;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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

    public Page<User> findAllByPattern(String pattern, int page, int size){
        return this.userRepository.findAllByPattern(pattern, PageRequest.of(page, size, Sort.by("email")));
    }

    public Page<User> findAllByPattern(String pattern, int page, int size, Sort sort){
        return this.userRepository.findAllByPattern(pattern, PageRequest.of(page, size, sort));
    }

    public Page<User> findAll (int page, int size, Sort sort){
        return this.userRepository.findAll(PageRequest.of(page, size, sort));
    }

    public Page<User> findAll (int page, int size){
        return this.userRepository.findAll(PageRequest.of(page, size, Sort.by("email")));
    }

    public User findByEmail (String email){
        return this.userRepository.findByEmail(email);
    }

}
