package fr.esgi.secureupload.files.infrastructures.controllers;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.infrastructures.SpringTestWithFiles;
import fr.esgi.secureupload.users.domain.entities.User;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest extends SpringTestWithFiles {

    @Autowired
    private MockMvc mockMvc;

    private final String FILES_API = "/files";

    @Test
    @WithAnonymousUser
    public void getFiles_WhenNotAuthenticated_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(FILES_API)).andExpect(status().isUnauthorized());
    }

    @Test
    public void getFiles_WhenAuthenticated_ShouldReturnMyFiles () throws Exception {

        User u = randomUser();
        List<File> myFiles = new ArrayList<>();
        for (File f: files){
            if (f.getOwner().getId().equals(u.getId()))
                myFiles.add(f);
        }

        ResultActions res = this.mockMvc.perform(
                get(FILES_API).with(user(u.getId()).roles("USER")))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content", hasSize(myFiles.size())))
                    .andExpect(status().isOk());

        for (int i = 0; i < myFiles.size(); i ++){
            String path = "$.data.content[" + i + "]";
            res.andExpect(jsonPath(path + ".id").isNotEmpty())
                    .andExpect(jsonPath(path + ".name").isNotEmpty())
                    .andExpect(jsonPath(path + ".type").isNotEmpty())
                    .andExpect(jsonPath(path + ".status").isString())
                    .andExpect(jsonPath(path + ".size").isNumber())
                    .andExpect(jsonPath(path + ".owner").isMap());
        }
    }

    @Test
    @WithAnonymousUser
    public void getFile_WhenUnauthenticated_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(get(FILES_API + "/" + randomFile().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getFile_WhenFileDoesntBelongToMe_ShouldReturnUnauthorized () throws Exception {
        this.mockMvc.perform(
                get(FILES_API + "/" + randomFile().getId())
                        .with(user("someid")
                                .roles("USER")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getFile_WhenFileBelongsToMe_ShouldReturnFile () throws Exception {
        File f = randomFile();
        this.mockMvc.perform(
                get(FILES_API + "/" + f.getId())
                        .with(user(f.getOwner().getId())
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.name").isNotEmpty())
                .andExpect(jsonPath("data.type").isNotEmpty())
                .andExpect(jsonPath("data.status").isString())
                .andExpect(jsonPath("data.size").isNumber())
                .andExpect(jsonPath("data.owner").isMap());
    }

    @Test
    @WithMockUser
    public void getFile_WhenDoesNotExist_ShouldReturnNotFound () throws Exception {
        this.mockMvc.perform(get(FILES_API + "/wrong"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    public void uploadFile_WhenNotAuthenticated_ShouldReturnUnauthorized () throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
        this.mockMvc.perform(multipart(FILES_API).file(file))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void uploadFile_WhenNoFile_ShouldReturnBadRequest () throws Exception {
        this.mockMvc.perform(multipart(FILES_API))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void uploadFile_WhenEmptyFile_ShouldReturnBadRequest () throws Exception {
        byte [] empty = new byte[0];
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", empty);
        this.mockMvc.perform(multipart(FILES_API).file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void uploadFile_WhenValidFile_ShouldReturnCreated () throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());

        this.mockMvc.perform(multipart(FILES_API)
                    .file(file)
                    .with(user(randomUser().getId())
                            .roles("USER")))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.name").isNotEmpty())
                .andExpect(jsonPath("data.type").isNotEmpty())
                .andExpect(jsonPath("data.status").isNotEmpty())
                .andExpect(jsonPath("data.size").isNumber())
                .andExpect(jsonPath("data.owner").isMap())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void downloadFile_WhenFileDoesntExist_ShouldReturnNotFound () throws Exception {

        this.mockMvc.perform(get(FILES_API + "/doesnotexist/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void downloadFile_ShouldReturnFile () throws Exception {

        File f = randomFile();



        String data = this.mockMvc.perform(get(FILES_API + "/" + f.getId() + "/download")
                .with(user(f.getOwner().getId()).roles("USER")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

    }
}


