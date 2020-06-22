package fr.esgi.secureupload;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeAll
    static void addTestUsers(@Autowired UserService userService){
        for (int i = 0; i < 100; i ++){
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
        this.mockMvc.perform(
                get(USERS_API_PATH)
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
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
