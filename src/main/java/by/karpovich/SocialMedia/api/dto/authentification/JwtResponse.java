package by.karpovich.SocialMedia.api.dto.authentification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class JwtResponse {

    private Long id;
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private List<String> roles;
}
