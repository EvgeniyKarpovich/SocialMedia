package by.karpovich.SocialMedia.api.controller;

import by.karpovich.SocialMedia.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PutMapping("/requests/send/{userId}")
    public void sendFriendRequest(@PathVariable("userId") Long userId,
                                  @RequestHeader(value = "Authorization") String authorization) {
        userServiceImpl.sendFriendRequest(authorization, userId);
    }

    @PutMapping("/requests/accept/{reqId}")
    public void acceptRequest(@PathVariable("reqId") Long reqId,
                              @RequestHeader(value = "Authorization") String authorization) {
        userServiceImpl.acceptRequest(authorization, reqId);
    }

    @PutMapping("/requests/reject/{reqId}")
    public void rejectRequest(@PathVariable("reqId") Long reqId,
                              @RequestHeader(value = "Authorization") String authorization) {
        userServiceImpl.rejectRequest(authorization, reqId);
    }

    @PutMapping("/requests/unsubscribe/{reqId}")
    public void unsubscribe(@PathVariable("reqId") Long reqId,
                            @RequestHeader(value = "Authorization") String authorization) {
        userServiceImpl.unsubscribe(authorization, reqId);
    }

    @GetMapping("/followers")
    public List<String> getFollowers(@RequestHeader(value = "Authorization") String authorization) {
        return userServiceImpl.getFollowers(authorization);
    }

    @GetMapping("/friends")
    public List<String> getFriends(@RequestHeader(value = "Authorization") String authorization) {
        return userServiceImpl.getFriends(authorization);
    }

    @GetMapping("/subscribers")
    public List<String> getSub(@RequestHeader(value = "Authorization") String authorization) {
        return userServiceImpl.getSubscribers(authorization);
    }
}
