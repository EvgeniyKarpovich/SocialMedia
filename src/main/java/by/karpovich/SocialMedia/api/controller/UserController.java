package by.karpovich.SocialMedia.api.controller;

import by.karpovich.SocialMedia.api.dto.post.PostDtoForSaveUpdate;
import by.karpovich.SocialMedia.api.dto.post.PostDtoOut;
import by.karpovich.SocialMedia.service.PostService;
import by.karpovich.SocialMedia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final PostService postService;
    private final UserService userService;

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

    @PutMapping("/posts/update/{postId}")
    public void updatePost(@PathVariable("postId") Long postId,
                           @RequestBody PostDtoForSaveUpdate postDto,
                           @RequestHeader(value = "Authorization") String authorization) {
        postService.updatePost(postDto, authorization, postId);
    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable("postId") Long postId,
                           @RequestHeader(value = "Authorization") String authorization) {
        postService.deletePost(postId, authorization);
    }

    @PutMapping("/proba/{userId}")
    public void request(@PathVariable("userId") Long userId,
                        @RequestHeader(value = "Authorization") String authorization) {
        userService.sendFriendRequest(authorization, userId);
    }

    @GetMapping("/followers")
    public List<String> aaaa(@RequestHeader(value = "Authorization") String authorization) {
        return userService.getFollowers(authorization);
    }

    @GetMapping("/friends")
    public List<String> bbbb(@RequestHeader(value = "Authorization") String authorization) {
        return userService.getFriends(authorization);
    }

    @PutMapping("/proba2/{reqId}")
    public void accept(@PathVariable("reqId") Long reqId,
                       @RequestHeader(value = "Authorization") String authorization) {
        userService.acceptRequest(authorization, reqId);
    }
}
