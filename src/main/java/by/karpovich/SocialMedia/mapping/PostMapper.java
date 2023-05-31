package by.karpovich.SocialMedia.mapping;

import by.karpovich.SocialMedia.api.dto.post.PostDtoForSaveUpdate;
import by.karpovich.SocialMedia.api.dto.post.PostDtoOut;
import by.karpovich.SocialMedia.exception.NotFoundModelException;
import by.karpovich.SocialMedia.jpa.entity.PostEntity;
import by.karpovich.SocialMedia.jpa.entity.UserEntity;
import by.karpovich.SocialMedia.jpa.repository.UserRepository;
import by.karpovich.SocialMedia.security.JwtUtils;
import by.karpovich.SocialMedia.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public PostEntity mapPostEntityFromPostDto(PostDtoForSaveUpdate dto, Long id) {
        if (dto == null) {
            return null;
        }

        return PostEntity.builder()
                .header(dto.getHeader())
                .text(dto.getText())
                .user(findUserByIdWhichWillReturnModel(id))
                .build();
    }

    public PostDtoOut mapPostDtoOutFromPostEntity(PostEntity entity) {
        if (entity == null) {
            return null;
        }

        return PostDtoOut.builder()
                .sender(entity.getUser().getUsername())
                .header(entity.getHeader())
                .text(entity.getText())
                .image(Utils.getImageAsResponseEntity(entity.getImage()))
                .dateOfCreation(Utils.mapStringFromInstant(entity.getDateOfCreation()))
                .build();

    }

    public UserEntity findUserByIdWhichWillReturnModel(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundModelException("User with id = " + id + "not found"));
    }
}
