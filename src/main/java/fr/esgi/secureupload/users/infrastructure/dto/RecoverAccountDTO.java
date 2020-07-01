package fr.esgi.secureupload.users.infrastructure.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RecoverAccountDTO {
    @NotBlank
    private String email;
}
