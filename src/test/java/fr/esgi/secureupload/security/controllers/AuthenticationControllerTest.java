package fr.esgi.secureupload.security.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.esgi.secureupload.utils.TestUtils;
import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.infrastructure.dto.LoginDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest extends SpringTestWithUsers {

    @Autowired
    private MockMvc mockMvc;

    private static final String AUTH_ENDPOINT = "/auth";

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void login_WithBadPassword_ShouldReturnUnauthorized () throws Exception {

        LoginDTO loginDto = new LoginDTO();
        loginDto.setUsername(users.get(0).getEmail());
        loginDto.setPassword("badpassword");

        this.mockMvc.perform(
                post(AUTH_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_WithGoodCredentials_ShouldReturnOKWithAuthorizationHeader () throws Exception {

        LoginDTO loginDto = new LoginDTO();
        loginDto.setUsername(users.get(0).getEmail());
        loginDto.setPassword(TestUtils.DEFAULT_PASSWORD);

        this.mockMvc.perform(
                post(AUTH_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, matchesPattern("^Bearer\\s.+$")));
    }
}
