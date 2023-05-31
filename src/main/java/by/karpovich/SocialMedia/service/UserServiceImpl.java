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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public void signUp(RegistrationForm dto) {

        userRepository.save(userMapper.mapEntityFromDtoForRegForm(dto));
    }

    @Override
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

    @Override
    public UserEntity findUserByName(String username) {
        Optional<UserEntity> userByName = userRepository.findByUsername(username);

        var entity = userByName.orElseThrow(
                () -> new NotFoundModelException(String.format("User with username = %s not found", username))
        );
        log.info("method findByName -  User with username = {} found", entity.getUsername());
        return entity;
    }

    @Override
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

    //ОБработать случай когда уже отправил запрос , чтобы нельзя было отправить еще один
    @Override
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

    @Override
    @Transactional
    public void acceptRequest(String authorization, Long requestId) {
        UserEntity receiver = findUserEntityByIdFromToken(authorization);

        FriendRequestEntity request = checkAvailabilityRequest(requestId, receiver);

        request.setRequestStatus(RequestStatus.ACCEPTED);
        receiver.getFriends().add(request.getSender());
        UserEntity sender = request.getSender();
        sender.getFriends().add(receiver);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    @Override
    @Transactional
    public void rejectRequest(String authorization, Long requestId) {
        UserEntity user = findUserEntityByIdFromToken(authorization);

        FriendRequestEntity request = checkAvailabilityRequest(requestId, user);
        request.setRequestStatus(RequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void unsubscribe(String authorization, Long requestId) {
        UserEntity user = findUserEntityByIdFromToken(authorization);

        FriendRequestEntity request = checkAvailabilityRequest(requestId, user);
        request.getSender().getSentFriendRequests().remove(request);

        userRepository.save(user);
        friendRequestRepository.save(request);
    }

    private FriendRequestEntity checkAvailabilityRequest(Long requestId, UserEntity user) {
        return user.getReceivedFriendRequests().stream()
                .filter(req -> req.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new NotFoundModelException("Request not found"));
    }

    public List<String> getFollowers(String auth) {
        UserEntity userEntityByIdFromToken = findUserEntityByIdFromToken(auth);
        List<UserEntity> followers = userEntityByIdFromToken.getFollowers();
        return followers.stream()
                .map(UserEntity::getUsername)
                .collect(Collectors.toList());
    }

    public List<String> getFriends(String auth) {
        UserEntity userEntityByIdFromToken = findUserEntityByIdFromToken(auth);

        return userEntityByIdFromToken.getFriends()
                .stream()
                .map(UserEntity::getUsername)
                .collect(Collectors.toList());
    }
}
