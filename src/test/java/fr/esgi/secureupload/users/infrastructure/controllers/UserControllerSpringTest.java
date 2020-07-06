package fr.esgi.secureupload.users.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import fr.esgi.secureupload.utils.TestUtils;
import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import fr.esgi.secureupload.users.infrastructure.dto.ConfirmMailDto;
import fr.esgi.secureupload.users.infrastructure.dto.RecoverAccountDTO;
import fr.esgi.secureupload.users.infrastructure.dto.ResetPasswordDTO;
import fr.esgi.secureupload.users.infrastructure.dto.UserDTO;
import fr.esgi.secureupload.users.domain.entities.User;

import fr.esgi.secureupload.users.domain.repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerSpringTest extends SpringTestWithUsers {

    @Autowired
    private MockMvc mockMvc;

    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;

    private final static String USERS_API_PATH = "/users";

    public UserControllerSpringTest(@Autowired  UserJpaRepository userJpaRepository){
        this.userRepository = new UserJpaRepositoryAdapter(userJpaRepository);
    }

    /* GET / */
    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_FromAdmin_ShouldListUsers () throws Exception {

        ResultActions res = this.mockMvc.perform(get(USERS_API_PATH)
                    .queryParam("limit", String.valueOf(admins.size() + users.size())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()", equalTo(users.size() + admins.size())));

        for (int i = 0; i < admins.size() + users.size(); i ++){
            String path = "$.data.content[" + i + "]";
            res.andExpect(jsonPath(path + ".id").isNotEmpty())
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
    public void getUsers_WithSearchParam_ShouldSearchUsersByPattern () throws Exception {

        User user = randomUser();

        // part of string search (email)
        this.mockMvc.perform(get(USERS_API_PATH).queryParam("search",user.getEmail().substring(0, 6)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].id", is(user.getId())));

        // full string search (id)
        this.mockMvc.perform(get(USERS_API_PATH).queryParam("search", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].id", is(user.getId())));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getUsers_ParamLimit_ShouldLimitResults () throws Exception {

        int limitParam = SpringTestWithUsers.users.size() / 2;

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

        String last = null;
        for (LinkedHashMap<String, ?> user: users){
            String email = (String)user.get("email");
            if (last != null) {
                Assertions.assertTrue(email.compareTo(last) <= 0);
            }
            last = email;
        }
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

    /* GET /{id} */
    @Test
    @WithAnonymousUser
    public void getUser_FromAnonymous_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + randomUser().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUser_AnyUserFromAdmin_ShouldReturnUser () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + randomUser().getId())
                .with(user(admins.get(0).getId()).roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void getUser_FromSelfUser_ShouldReturnSelfUser () throws Exception {
        User u = randomUser();
        this.mockMvc.perform(get(USERS_API_PATH + "/" + u.getId())
                .with(user(u.getId()).roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    public void getUser_FromAnotherUser_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(USERS_API_PATH + "/" + users.get(0).getId())
                .with(user(users.get(1).getId()).roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUser_WhenNotExist_ShouldReturnNotFound () throws Exception {
        User u = randomUser();
        this.mockMvc.perform(get(USERS_API_PATH + "/" + UUID.randomUUID().toString())
                .with(user(u.getId()).roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUser_WhenAuthorized_ShouldReturnUser () throws Exception {

        User user = randomUser();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(USERS_API_PATH + "/" + user.getId())
                .with(user(user.getId()).roles("USER"));

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(user.getId())))
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
        userDto.setEmail(this.testUtils.getRandomMail());
        userDto.setPassword("Password12345");

        this.mockMvc.perform(post(USERS_API_PATH)
                .content(new ObjectMapper().writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.data.admin", is(false)))
                .andExpect(jsonPath("$.data.confirmed", is(false)))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.recoveryToken").doesNotExist())
                .andExpect(jsonPath("$.data.confirmationToken").doesNotExist())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(status().isCreated());

        JSONObject message = this.testUtils.getSentMail(userDto.getEmail());

        Assertions.assertNotNull(message);

        User user = this.userRepository.findByEmail(userDto.getEmail()).orElse(null);
        Assertions.assertNotNull(user);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(userDto.getEmail(), headers.getJSONArray("To").get(0));
        Assertions.assertTrue(message.getString("Body").contains(user.getConfirmationToken()));
    }

    /* PUT /{id}/password */

    @Test
    public void resetPassword_WhenWrongBody_ShouldReturnBadRequest () throws Exception {

        ResetPasswordDTO resetPasswordDto = new ResetPasswordDTO();
        resetPasswordDto.setCurrentPassword("abc123");
        resetPasswordDto.setNewPassword("abc123");

        User user = randomUser();

        this.mockMvc.perform(put(USERS_API_PATH + "/" + user.getId() + "/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(user(user.getId()).roles("USER"))
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

        User user = randomUser();

        this.mockMvc.perform(put(USERS_API_PATH + "/" + user.getId() + "/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(resetPasswordDto))
                .with(user(user.getId()).roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void resetPassword_WhenSelfAndValidBody_ShouldChangePassword () throws Exception {

        ResetPasswordDTO resetPasswordDto = new ResetPasswordDTO();
        resetPasswordDto.setCurrentPassword(TestUtils.DEFAULT_PASSWORD);
        resetPasswordDto.setNewPassword("MyNewPassword12345");

        User user = randomUser();

        this.mockMvc.perform(put(USERS_API_PATH + "/" + user.getId() + "/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(resetPasswordDto))
                .with(user(user.getId()).roles("USER")))
                .andExpect(status().isNoContent());

        User modified = this.userRepository.findById(user.getId()).orElse(null);
        if (modified == null)
            Assertions.fail();
        Assertions.assertNotEquals(modified.getPassword(), user.getPassword());
    }

    /* POST /{id}/confirm */

    @Test
    @WithMockUser(roles = { "ADMIN", "USER" })
    public void confirmUser_WhenAuthenticated_ShouldReturnUnauthorized () throws Exception {

        User user = randomUser();

        ConfirmMailDto confirmMailDto = new ConfirmMailDto();
        confirmMailDto.setConfirmationToken(user.getConfirmationToken());

        this.mockMvc.perform(
                post(String.format("%s/%s/confirm", USERS_API_PATH, user.getId()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(confirmMailDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void confirmUser_WhenAnonymousAndInvalidToken_ShouldReturnForbidden () throws Exception {
        ConfirmMailDto confirmMailDto = new ConfirmMailDto();
        confirmMailDto.setConfirmationToken("badtoken");

        User user = randomUser();

        this.mockMvc.perform(
                post(String.format("%s/%s/confirm", USERS_API_PATH, user.getId()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(confirmMailDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void confirmUser_WhenAnonymousAndValidToken_ShouldReturnNoContentAndConfirmUser () throws Exception {

        User user = randomUser();

        ConfirmMailDto confirmMailDto = new ConfirmMailDto();
        confirmMailDto.setConfirmationToken(user.getConfirmationToken());

        this.mockMvc.perform(
                post(String.format("%s/%s/confirm", USERS_API_PATH, user.getId()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(confirmMailDto)))
                .andExpect(status().isNoContent());

        User updated = this.userRepository.findById(user.getId()).orElse(null);
        if (updated == null){
            Assertions.fail();
        }
        Assertions.assertTrue(updated.isConfirmed());
    }

    /* DELETE /{id} */

    @Test
    @WithAnonymousUser
    public void deleteUser_WhenAnonymous_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(delete(USERS_API_PATH + "/" + randomUser().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void deleteUser_WhenNotSelf_ShouldReturnForbidden () throws Exception {
        this.mockMvc.perform(delete(USERS_API_PATH + "/" + randomUser().getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void deleteUser_WhenAdmin_ShouldDeleteUserAndReturnNoContent () throws Exception {
        this.mockMvc.perform(delete(USERS_API_PATH + "/" + users.get(0).getId()))
                .andExpect(status().isNoContent());
        Assertions.assertNull(this.userRepository.findById(users.get(0).getId())
                .orElse(null));
        users.remove(0);
    }

    @Test
    public void deleteUser_WhenSelf_ShouldDeleteUserAndReturnNoContent () throws Exception {
        this.mockMvc.perform(
                delete(USERS_API_PATH + "/" + users.get(0).getId())
                .with(user(users.get(0).getId())
                        .roles("USER")))
                .andExpect(status().isNoContent());
        Assertions.assertNull(this.userRepository.findById(users.get(0).getId())
                .orElse(null));
        users.remove(0);
    }

    /* POST /recovery */

    @Test
    @WithMockUser
    public void createRecoveryToken_WhenAuthenticated_ShouldReturnUnauthorized () throws Exception {

        RecoverAccountDTO recoverAccountDto = new RecoverAccountDTO();
        recoverAccountDto.setEmail(randomUser().getEmail());

        this.mockMvc.perform(
                post(USERS_API_PATH + "/recovery")
                .content(new ObjectMapper().writeValueAsString(recoverAccountDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void createRecoveryToken_WhenUnknownMail_ShouldReturnOk () throws Exception {

        RecoverAccountDTO recoverAccountDto = new RecoverAccountDTO();
        recoverAccountDto.setEmail("unknown@domain.com");

        this.mockMvc.perform(
                post(USERS_API_PATH + "/recovery")
                        .content(new ObjectMapper().writeValueAsString(recoverAccountDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void createRecoveryToken_WhenValidMail_ShouldReturnOkAndSendRecoveryMail () throws Exception {

        RecoverAccountDTO recoverAccountDto = new RecoverAccountDTO();
        recoverAccountDto.setEmail(randomUser().getEmail());

        this.mockMvc.perform(
                post(USERS_API_PATH + "/recovery")
                        .content(new ObjectMapper().writeValueAsString(recoverAccountDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        JSONObject message = this.testUtils.getSentMail(recoverAccountDto.getEmail());

        Assertions.assertNotNull(message);

        User user = this.userRepository.findByEmail(recoverAccountDto.getEmail()).orElse(null);
        Assertions.assertNotNull(user);

        JSONObject headers = message.getJSONObject("Headers");

        Assertions.assertEquals(user.getEmail(), headers.getJSONArray("To").get(0));
        Assertions.assertTrue(message.getString("Body").contains(user.getRecoveryToken()));

    }
}
