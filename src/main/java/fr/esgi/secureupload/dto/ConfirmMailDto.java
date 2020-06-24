package fr.esgi.secureupload.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfirmMailDto {
    @NotBlank
    private String confirmationToken;
}
