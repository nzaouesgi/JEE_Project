package fr.esgi.secureupload.users.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {

    @Email
    private String username;

    @NotBlank
    private String password;
}