package fr.esgi.secureupload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import fr.esgi.secureupload.dto.ResetPasswordDTO;
import fr.esgi.secureupload.dto.UserDTO;
import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestUtils testUtils;

    private final static String USERS_API_PATH = "/users";
    private final static int TEST_USERS = 30;
    private final static int TEST_ADMIN = 3;
    private final static ArrayList<User> users = new ArrayList<>();
    private final static ArrayList<User> admins = new ArrayList<>();

    @BeforeAll
    public static void addTestUsers(@Autowired UserService userService, @Autowired TestUtils testUtils){

        for (int i = 0; i < TEST_USERS; i ++){
            users.add(User.builder()
                    .isAdmin(false)
                    .isConfirmed(true)
                    .password("Password12345")
                    .email(testUtils.getRandomMail()).build());
            userService.save(users.get(i));
        }
        for (int i = 0; i < TEST_ADMIN; i ++){
            admins.add(User.builder()
                    .isAdmin(true)
                    .isConfirmed(true)
                    .password("Password12345")
                    .email(testUtils.getRandomMail()).build());
            userService.save(admins.get(i));
        }
    }

    /* GET / */
    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_FromAdmin_ShouldListUsers () throws Exception {

        ResultActions res = this.mockMvc.perform(get(USERS_API_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()", greaterThanOrEqualTo(TEST_ADMIN + TEST_USERS)));

        for (int i = 0; i < TEST_USERS + TEST_ADMIN; i ++){
            String path = "$.data.content[" + i + "]";
            res.andExpect(jsonPath(path + ".uuid").isNotEmpty())
                    .andExpect(jsonPath(path + ".email").isNotEmpty())
                    .andExpect(jsonPath(path + ".admin").isBoolean())
                    .andExpect(jsonPath(path + ".confirmed").isBoolean())
                    .andExpect(jsonPath(path + ".password").doesNotExist())
                    .andExpect(jsonPath(path + ".recoveryToken").doesNotExist())
                    .andExpect(jsonPath(path + ".confirmationToken").doesNotExist());
        }
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_ParamLimit_ShoudLimitResults () throws Exception {

        int limitParam = TEST_USERS / 2;

        this.mockMvc.perform(get(USERS_API_PATH)
                        .param("limit", String.valueOf(limitParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(limitParam)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_OrderBy_ShouldOrderByField () throws Exception {

        MvcResult result = this.mockMvc.perform(get(USERS_API_PATH)
                        .param("orderBy", "email")
                        .param("orderMode", "desc"))
                .andExpect(status().isOk())
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
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_OrderByPrivateField_ShouldReturnForbidden () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH).param("orderBy", "password"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void getUsers_FromUser_ShoudReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void getUsers_FromAnonymous_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH))
                .andExpect(status()
                        .isUnauthorized());
    }

    /* GET /{uuid} */
    @Test
    @WithAnonymousUser
    public void getUser_FromAnonymous_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + users.get(0)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUser_AnyUserFromAdmin_ShouldReturnUser () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + users.get(0).getUuid())
                .with(user(admins.get(0).getEmail()).roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void getUser_FromSelfUser_ShouldReturnSelfUser () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + users.get(0).getUuid())
                .with(user(users.get(0).getEmail()).roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    public void getUser_FromAnotherUser_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + users.get(1).getUuid())
                .with(user(users.get(0).getEmail()).roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUser_WhenNotExist_ShouldReturnNotFound () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + UUID.randomUUID().toString())
                .with(user(users.get(0).getEmail()).roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUser_WhenAuthorized_ShouldReturnUser () throws Exception {

        User user = users.get(0);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(USERS_API_PATH + "/" + user.getUuid())
                .with(user(user.getEmail()).roles("USER"));

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid", is(user.getUuid())))
                .andExpect(jsonPath("$.data.email", is(user.getEmail())))
                .andExpect(jsonPath("$.data.admin", is(user.isAdmin())))
                .andExpect(jsonPath("$.data.confirmed", is(user.isConfirmed())))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.recoveryToken").doesNotExist())
                .andExpect(jsonPath("$.data.confirmationToken").doesNotExist());
    }

    /* POST / */

    @Test
    @WithMockUser(roles = { "USER", "ADMIN"})
    public void createUser_WhenAuthenticated_ShouldReturnUnauthorized () throws Exception {

        UserDTO userDto = new UserDTO();
        userDto.setEmail("mymail@domain.com");
        userDto.setPassword("SomePassword123");

        this.mockMvc.perform(post(USERS_API_PATH)
                .content(new ObjectMapper().writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void createUser_WhenWrongBody_ShouldReturnBadRequest () throws Exception  {

        UserDTO userDto = new UserDTO();
        userDto.setEmail("notanemail");
        userDto.setPassword("abc");

        this.mockMvc.perform(post(USERS_API_PATH)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    @WithAnonymousUser
    public void createUser_WhenAnonymous_ShouldCreateUserAndSendConfirmationMail () throws Exception {

        UserDTO userDto = new UserDTO();
        userDto.setEmail(testUtils.getRandomMail());
        userDto.setPassword("Password12345");

        this.mockMvc.perform(post(USERS_API_PATH)
                .content(new ObjectMapper().writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.uuid").isNotEmpty())
                .andExpect(jsonPath("$.data.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.data.admin", is(false)))
                .andExpect(jsonPath("$.data.confirmed", is(false)))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.recoveryToken").doesNotExist())
                .andExpect(jsonPath("$.data.confirmationToken").doesNotExist())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(status().isCreated());

        JSONObject message = testUtils.getSentMail(userDto.getEmail());

        Assertions.assertNotNull(message);

        User user = userService.findByEmail(userDto.getEmail()).orElse(null);
        Assertions.assertNotNull(user);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(userDto.getEmail(), headers.getJSONArray("To").get(0));
        Assertions.assertTrue(message.getString("Body").contains(user.getConfirmationToken()));
    }

    /* GET /{uuid}/confirm */


    /* PUT /{uuid}/password */

    @Test
    @WithAnonymousUser
    public void resetPassword_WhenAnonymous_ShouldReturnUnauthorized () throws Exception {

        ResetPasswordDTO resetPasswordDto = new ResetPasswordDTO();
        resetPasswordDto.setCurrentPassword("Something123");
        resetPasswordDto.setNewPassword("321gnihtemoS");

        this.mockMvc.perform(put(USERS_API_PATH + "/" + users.get(0).getUuid() + "/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(resetPasswordDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void resetPassword_WhenWrongBody_ShouldReturnBadRequest () throws Exception {

        ResetPasswordDTO resetPasswordDto = new ResetPasswordDTO();
        resetPasswordDto.setCurrentPassword("abc123");
        resetPasswordDto.setNewPassword("abc123");

        this.mockMvc.perform(put(USERS_API_PATH + "/" + users.get(0).getUuid() + "/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(user(users.get(0).getEmail()).roles("USER"))
                .content(new ObjectMapper().writeValueAsString(resetPasswordDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    public void resetPassword_WhenSelfAndWrongPassword_ShouldReturnForbidden () throws Exception {

        ResetPasswordDTO resetPasswordDto = new ResetPasswordDTO();
        resetPasswordDto.setCurrentPassword("BadPassword :(");
        resetPasswordDto.setNewPassword("MyNewPassword12345");

        this.mockMvc.perform(put(USERS_API_PATH + "/" + users.get(0).getUuid() + "/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(resetPasswordDto))
                .with(user(users.get(0).getEmail()).roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void resetPassword_WhenSelfAndValidBody_ShouldChangePassword () throws Exception {

        ResetPasswordDTO resetPasswordDto = new ResetPasswordDTO();
        resetPasswordDto.setCurrentPassword("Password12345");
        resetPasswordDto.setNewPassword("MyNewPassword12345");

        this.mockMvc.perform(put(USERS_API_PATH + "/" + users.get(0).getUuid() + "/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(resetPasswordDto))
                .with(user(users.get(0).getEmail()).roles("USER")))
                .andExpect(status().isNoContent());

        User modified = this.userService.findById(users.get(0).getUuid()).orElse(null);
        if (modified == null)
            Assertions.fail();
        Assertions.assertNotEquals(modified.getPassword(), users.get(0).getPassword());
    }
}
