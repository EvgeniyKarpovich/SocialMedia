package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.post.PostDtoForSaveUpdate;
import by.karpovich.SocialMedia.api.dto.post.PostDtoOut;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PostService {

    PostDtoOut save(PostDtoForSaveUpdate dto, String authorization);

    void addImage(Long postId, String authorization, MultipartFile file);

    void updatePost(PostDtoForSaveUpdate dto, String authorization, Long postId);

    Map<String, Object> findAll(int page, int size, String authorization);

    void deletePost(Long postId, String authorization);
}
