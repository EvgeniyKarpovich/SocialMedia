package by.karpovich.SocialMedia.mapping;

import by.karpovich.SocialMedia.api.dto.authentification.RegistrationForm;
import by.karpovich.SocialMedia.jpa.entity.UserEntity;
import by.karpovich.SocialMedia.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private static final String ROLE_USER = "ROLE_USER";
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserEntity mapEntityFromDtoForRegForm(RegistrationForm dto) {
        if (dto == null) {
            return null;
        }

        return UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .roles(roleService.findRoleByName(ROLE_USER))
                .build();
    }
}
