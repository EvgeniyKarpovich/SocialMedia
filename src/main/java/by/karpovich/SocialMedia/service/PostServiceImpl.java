package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.post.PostDtoForSaveUpdate;
import by.karpovich.SocialMedia.api.dto.post.PostDtoOut;
import by.karpovich.SocialMedia.exception.NotFoundModelException;
import by.karpovich.SocialMedia.jpa.entity.PostEntity;
import by.karpovich.SocialMedia.jpa.entity.UserEntity;
import by.karpovich.SocialMedia.jpa.repository.PostRepository;
import by.karpovich.SocialMedia.mapping.PostMapper;
import by.karpovich.SocialMedia.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserServiceImpl userServiceImpl;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostDtoOut save(PostDtoForSaveUpdate dto, String authorization) {
        Long userIdFromToken = userServiceImpl.getUserIdFromToken(authorization);

        PostEntity postEntity = postMapper.mapPostEntityFromPostDto(dto, userIdFromToken);

        return postMapper.mapPostDtoOutFromPostEntity(postRepository.save(postEntity));
    }

    @Override
    @Transactional
    public void addImage(Long postId, String authorization, MultipartFile file) {
        PostEntity postEntity = checkingIfUserHasPost(authorization, postId);

        postEntity.setImage(Utils.saveFile(file));
        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public void updatePost(PostDtoForSaveUpdate dto, String authorization, Long postId) {
        PostEntity postEntity = checkingIfUserHasPost(authorization, postId);

        postEntity.setHeader(dto.getHeader());
        postEntity.setText(dto.getText());
        postEntity.setId(postId);

        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String authorization) {
        PostEntity postEntity = checkingIfUserHasPost(authorization, postId);
        if (postRepository.findById(postEntity.getId()).isEmpty()) {
            throw new NotFoundModelException(String.format("Post with id = %s not found", postId));
        }
        postRepository.deleteById(postEntity.getId());
    }

    //Проверяю принадлежит ли пост пользователю который проводит с ним манипуляции, если нет - ошибка
    private PostEntity checkingIfUserHasPost(String authorization, Long postId) {
        UserEntity userEntityByIdFromToken = userServiceImpl.findUserEntityByIdFromToken(authorization);
        return userEntityByIdFromToken.getPosts().stream()
                .filter(post -> post.getId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You can't do it"));
    }
}