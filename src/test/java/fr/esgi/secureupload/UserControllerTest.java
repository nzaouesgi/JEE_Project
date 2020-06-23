package fr.esgi.secureupload;

import com.jayway.jsonpath.JsonPath;
import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private final static String USERS_API_PATH = "/users";
    private final static int TEST_USERS = 100;

    @BeforeAll
    static void addTestUsers(@Autowired UserService userService){
        for (int i = 0; i < TEST_USERS; i ++){
            userService.save(User.builder()
                    .isAdmin(false)
                    .isConfirmed(true)
                    .password("Password12345")
                    .email(UserTest.getRandomMail()).build());
        }
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_ShouldListUsers_FromAdmin () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_ParamLimit () throws Exception {

        int limitParam = TEST_USERS / 2;

        this.mockMvc.perform(get(USERS_API_PATH)
                        .param("limit", String.valueOf(limitParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(limitParam)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_OrderByEmail () throws Exception {

        MvcResult result = this.mockMvc.perform(get(USERS_API_PATH)
                        .param("orderBy", "email")
                        .param("orderMode", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(TEST_USERS)))
                .andReturn();

        ArrayList<LinkedHashMap<String, ?>> users = JsonPath.read(result
                .getResponse()
                .getContentAsString(), "$.data.content[*]");

        String current = null;
        for (LinkedHashMap<String, ?> user: users){
            String email = (String)user.get("email");
            if (current != null) {
                Assertions.assertTrue(email.compareTo(current) > 0);
            }
            current = email;
        }
    }

    @Test
    @WithMockUser
    public void getUsers_UnauthorizedFromUser () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void getUsers_UnauthorizedFromAnonymous () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH))
                .andExpect(status()
                        .isUnauthorized());
    }



}
