package fr.esgi.secureupload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import fr.esgi.secureupload.controllers.UserController;
import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@WebMvcTest(UserController.class)
@ContextConfiguration
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserController controller;

    @Autowired
    private UserService userService;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void testGetUsers() {
        final String baseURL = "http://localhost:" + port + "/users";
        assertThat(restTemplate.getForObject(baseURL, String.class)).contains("\"status\":401")
                .contains("Access denied");
    }

    @Test
    public void testGetUsersAdmin() throws JSONException, JsonMappingException, JsonProcessingException {
        final String baseURL = "http://localhost:" + port + "/users";

        var user = User.builder().email("admin@example.org").password("password").build();
        userService.save(user);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var json = new JSONObject();
        json.put("username", "admin@example.org");
        json.put("password", "password");

        var authRequest = new HttpEntity<String>(json.toString(), headers);
        HttpEntity<String> res = restTemplate.exchange("http://localhost:" + port + "/auth", HttpMethod.POST,
                authRequest, String.class);

        System.out.println(res.getBody());
        System.out.println(res.getHeaders().get("Authorization"));

        headers = new HttpHeaders();
        headers.add("Authorization", res.getHeaders().get("Authorization").get(0));
        ResponseEntity<String> entity = restTemplate.exchange(baseURL, HttpMethod.GET, new HttpEntity<Object>(headers),
                String.class);

        assertThat(entity.getBody()).contains("\"status\":200").contains("[]");
    }

    @Test
    public void testGetUser() {

    }

    @Test
    public void testCreateUser() {

    }

    @Test
    public void testResetPassword() {

    }
}