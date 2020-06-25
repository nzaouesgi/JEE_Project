package fr.esgi.secureupload.security.controllers;

import fr.esgi.secureupload.security.jwt.JWTProvider;
import fr.esgi.secureupload.users.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JWTProvider jwtProvider;
    private final AuthenticationManagerBuilder authManager;

    @Autowired
    public AuthenticationController(JWTProvider jwtProvider, AuthenticationManagerBuilder authManager) {
        this.jwtProvider = jwtProvider;
        this.authManager = authManager;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        Authentication authentication = authManager.getObject().authenticate(authenticationToken);

        String token = jwtProvider.createToken(authentication);
        response.setHeader(AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
