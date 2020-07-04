package fr.esgi.secureupload.users.infrastructure.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfirmMailDto {
    @NotBlank
    private String confirmationToken;
}
