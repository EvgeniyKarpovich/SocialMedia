package by.karpovich.SocialMedia.api.controller;

import by.karpovich.SocialMedia.api.dto.post.PostDtoForSaveUpdate;
import by.karpovich.SocialMedia.api.dto.post.PostDtoOut;
import by.karpovich.SocialMedia.service.PostServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post Controller", description = "Post API")
public class PostController {

    private final PostServiceImpl postServiceImpl;

    @PostMapping
    public PostDtoOut save(@RequestBody PostDtoForSaveUpdate postDto,
                           @RequestHeader(value = "Authorization") String authorization) {
        return postServiceImpl.save(postDto, authorization);
    }

    @PostMapping(value = "/images/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addImage(@PathVariable("postId") Long postId,
                         @RequestHeader(value = "Authorization") String authorization,
                         @RequestPart("file") MultipartFile file) {
        postServiceImpl.addImage(postId, authorization, file);
    }

    @PutMapping("/{postId}")
    public void updatePost(@PathVariable("postId") Long postId,
                           @RequestBody PostDtoForSaveUpdate postDto,
                           @RequestHeader(value = "Authorization") String authorization) {
        postServiceImpl.updatePost(postDto, authorization, postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable("postId") Long postId,
                           @RequestHeader(value = "Authorization") String authorization) {
        postServiceImpl.deletePost(postId, authorization);
    }

    @GetMapping
    public Map<String, Object> findAll(@RequestHeader(value = "Authorization") String authorization,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return postServiceImpl.findAll(page, size, authorization);
    }
}
