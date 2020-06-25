package fr.esgi.secureupload.security.services;

import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.usecases.FindUserByEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private FindUserByEmail findUserByEmail;

    public UserDetailsServiceImpl (@Autowired FindUserByEmail findUserByEmail){
        this.findUserByEmail = findUserByEmail;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {
            User user = this.findUserByEmail.execute(email);

            return org.springframework.security.core.userdetails.User
                    .withUsername(email)
                    .accountLocked(!user.isConfirmed())
                    .password(user.getPassword())
                    .roles(user.isAdmin() ? "ADMIN" : "USER")
                    .build();

        } catch (UserNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
