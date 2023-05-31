package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.authentification.JwtResponse;
import by.karpovich.SocialMedia.api.dto.authentification.LoginForm;
import by.karpovich.SocialMedia.api.dto.authentification.RegistrationForm;
import by.karpovich.SocialMedia.exception.NotFoundModelException;
import by.karpovich.SocialMedia.jpa.entity.FriendRequestEntity;
import by.karpovich.SocialMedia.jpa.entity.RequestStatus;
import by.karpovich.SocialMedia.jpa.entity.UserEntity;
import by.karpovich.SocialMedia.jpa.repository.FriendRequestRepository;
import by.karpovich.SocialMedia.jpa.repository.UserRepository;
import by.karpovich.SocialMedia.mapping.UserMapper;
import by.karpovich.SocialMedia.security.JwtUtils;
import by.karpovich.SocialMedia.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final FriendRequestRepository friendRequestRepository;

    @Transactional
    public void signUp(RegistrationForm dto) {

        userRepository.save(userMapper.mapEntityFromDtoForRegForm(dto));
    }

    @Transactional
    public JwtResponse signIn(LoginForm loginForm) {
        String username = loginForm.getUsername();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));

        UserEntity userByName = findUserByName(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userByName.getUsername(), userByName.getId());

        return JwtResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(mapStringRolesFromUserDetails(userDetails))
                .type("Bearer")
                .token(jwt)
                .build();
    }

    private List<String> mapStringRolesFromUserDetails(UserDetailsImpl userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public UserEntity findUserByName(String username) {
        Optional<UserEntity> userByName = userRepository.findByUsername(username);

        var entity = userByName.orElseThrow(
                () -> new NotFoundModelException(String.format("User with username = %s not found", username))
        );
        log.info("method findByName -  User with username = {} found", entity.getUsername());
        return entity;
    }

    public UserEntity findUserByIdWhichWillReturnModel(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundModelException("User with id = " + id + "not found"));
    }

    public UserEntity findUserEntityByIdFromToken(String token) {
        Long userIdFromToken = getUserIdFromToken(token);

        return findUserByIdWhichWillReturnModel(userIdFromToken);
    }

    public Long getUserIdFromToken(String authorization) {
        String token = authorization.substring(7);
        String userIdFromJWT = jwtUtils.getUserIdFromJWT(token);
        return Long.parseLong(userIdFromJWT);
    }

    @Transactional
    public void sendFriendRequest(String authorization, Long recipientRequestId) {
        UserEntity sender = findUserEntityByIdFromToken(authorization);
        UserEntity recipient = findUserByIdWhichWillReturnModel(recipientRequestId);

        FriendRequestEntity request = new FriendRequestEntity();
        request.setRequestStatus(RequestStatus.PENDING);
        request.setSender(sender);
        request.setReceiver(recipient);

        friendRequestRepository.save(request);

        sender.getSentFriendRequests().add(request);
        recipient.getReceivedFriendRequests().add(request);
        recipient.getFollowers().add(sender);

        userRepository.save(sender);
        userRepository.save(recipient);
    }

    public String getFollowers(String auth) {
        UserEntity userEntityByIdFromToken = findUserEntityByIdFromToken(auth);
        Set<UserEntity> followers = userEntityByIdFromToken.getFollowers();
        UserEntity userEntity = followers.stream().findFirst()
                .get();
        return userEntity.getUsername();
    }
}
