package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.post.PostDtoForSaveUpdate;
import by.karpovich.SocialMedia.api.dto.post.PostDtoOut;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    PostDtoOut save(PostDtoForSaveUpdate dto, String authorization);

    void addImage(Long postId, String authorization, MultipartFile file);

    void updatePost(PostDtoForSaveUpdate dto, String authorization, Long postId);

    void deletePost(Long postId, String authorization);
}
