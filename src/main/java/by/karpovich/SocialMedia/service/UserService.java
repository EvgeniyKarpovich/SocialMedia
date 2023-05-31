package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.authentification.JwtResponse;
import by.karpovich.SocialMedia.api.dto.authentification.LoginForm;
import by.karpovich.SocialMedia.api.dto.authentification.RegistrationForm;
import by.karpovich.SocialMedia.jpa.entity.UserEntity;

public interface UserService {

    void signUp(RegistrationForm dto);
    JwtResponse signIn(LoginForm loginForm);
    UserEntity findUserByName(String username);
    UserEntity findUserByIdWhichWillReturnModel(Long id);
    void sendFriendRequest(String authorization, Long recipientRequestId);
    void acceptRequest(String authorization, Long requestId);
    void rejectRequest(String authorization, Long requestId);
    void unsubscribe(String authorization, Long requestId);

}
