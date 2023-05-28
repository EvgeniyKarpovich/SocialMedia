package by.karpovich.SocialMedia.api.dto.authentification;

import by.karpovich.SocialMedia.api.validation.emailValidator.ValidEmail;
import by.karpovich.SocialMedia.api.validation.usernameValidation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationForm {

    @ValidUsername
    @NotBlank(message = "Enter name")
    private String username;

    @ValidEmail
    @NotBlank(message = "Enter email")
    private String email;

    @NotBlank(message = "Enter password")
    private String password;
}