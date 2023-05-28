package by.karpovich.SocialMedia.api.controller;

import by.karpovich.SocialMedia.api.dto.post.PostDtoForSaveUpdate;
import by.karpovich.SocialMedia.api.dto.post.PostDtoOut;
import by.karpovich.SocialMedia.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final PostService postService;

    @PostMapping("/posts")
    public PostDtoOut save(@RequestBody PostDtoForSaveUpdate postDto,
                           @RequestHeader(value = "Authorization") String authorization) {
        return postService.save(postDto, authorization);
    }

    @PutMapping("/posts/{postId}")
    public void addImage(@PathVariable("postId") Long postId,
                         @RequestHeader(value = "Authorization") String authorization,
                         @RequestPart("file") MultipartFile file) {
        postService.addImage(postId, authorization, file);
    }
}
