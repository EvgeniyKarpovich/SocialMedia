package by.karpovich.SocialMedia.api.dto.authentification;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {

    @NotBlank(message = "Enter name")
    private String username;

    @NotBlank(message = "Enter password")
    private String password;
}