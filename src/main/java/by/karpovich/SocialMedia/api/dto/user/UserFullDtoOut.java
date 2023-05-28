package by.karpovich.SocialMedia.api.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFullDtoOut {

    private String username;

    private String email;

    private Double balance;
}
