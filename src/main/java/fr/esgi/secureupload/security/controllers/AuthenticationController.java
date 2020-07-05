package fr.esgi.secureupload.security.controllers;

import fr.esgi.secureupload.security.jwt.JWTProvider;
import fr.esgi.secureupload.users.infrastructure.dto.LoginDTO;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.domain.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.usecases.FindUserByEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JWTProvider jwtProvider;
    private final AuthenticationManagerBuilder authManager;
    private final FindUserByEmail findUserByEmail;

    public AuthenticationController(@Autowired JWTProvider jwtProvider, @Autowired AuthenticationManagerBuilder authManager, @Autowired FindUserByEmail findUserByEmail) {
        this.jwtProvider = jwtProvider;
        this.authManager = authManager;
        this.findUserByEmail = findUserByEmail;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response) {

        try {

            User found = findUserByEmail.execute(loginDTO.getUsername());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(found.getId(), loginDTO.getPassword());

            Authentication authentication = authManager.getObject().authenticate(authenticationToken);

            String token = jwtProvider.createToken(authentication);
            response.setHeader(AUTHORIZATION, String.join(" ", "Bearer", token));

        } catch (UserNotFoundException e){
            throw new AccessDeniedException("Login failed.");
        }
    }

}
