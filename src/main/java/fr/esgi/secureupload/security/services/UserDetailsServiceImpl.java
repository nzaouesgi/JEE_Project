package fr.esgi.secureupload.security.services;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.usecases.FindUserByEmail;
import fr.esgi.secureupload.users.usecases.FindUserById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private FindUserById findUserById;

    public UserDetailsServiceImpl (@Autowired FindUserById findUserById){
        this.findUserById = findUserById;
    }

    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        try {

            User user = this.findUserById.execute(id);

            return org.springframework.security.core.userdetails.User
                    .withUsername(id)
                    .accountLocked(!user.isConfirmed())
                    .password(user.getPassword())
                    .roles(user.isAdmin() ? "ADMIN" : "USER")
                    .build();

        } catch (UserNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
