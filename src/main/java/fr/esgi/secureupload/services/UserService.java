package fr.esgi.secureupload.services;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return this.userRepository.save(user);
    }


}
