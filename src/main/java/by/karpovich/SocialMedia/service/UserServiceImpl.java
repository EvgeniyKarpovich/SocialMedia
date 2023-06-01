package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.authentification.JwtResponse;
import by.karpovich.SocialMedia.api.dto.authentification.LoginForm;
import by.karpovich.SocialMedia.api.dto.authentification.RegistrationForm;
import by.karpovich.SocialMedia.exception.DuplicateException;
import by.karpovich.SocialMedia.exception.NotFoundModelException;
import by.karpovich.SocialMedia.exception.RejectedException;
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
                () -> new NotFoundModelException("User with id = " + id + " not found"));
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
    public void sendFriendRequest(String authorization, Long receiverId) {
        UserEntity sender = findUserEntityByIdFromToken(authorization);
        UserEntity receiver = findUserByIdWhichWillReturnModel(receiverId);

        //Нельзя отправлять самому себе)
        if (receiverId.equals(sender.getId())) {
            throw new RuntimeException("You cannot send a request to yourself");
        }

        //проверяю наличие запроса у получателя , если отправитель уже послал запрос - ошибка
        boolean checkRequestFromReceiver = sender.getSentFriendRequests().stream()
                .anyMatch(req -> req.getReceiver().getId().equals(receiver.getId()));
        if (checkRequestFromReceiver) {
            throw new DuplicateException("You have already submitted a request");
        }

        //Ищу запрос, если он есть и его статус Pending - ошибка , если нет - создаю новый запрос
        FriendRequestEntity request = findSenderRequestByReceiver(sender, receiver);
        if (request != null && findRequestByIdFromUser(request.getId(), sender).getRequestStatus().equals(RequestStatus.PENDING)) {
            throw new RejectedException("Request being processed ");
        }

        FriendRequestEntity newRequest = FriendRequestEntity.builder()
                .requestStatus(RequestStatus.PENDING)
                .sender(sender)
                .receiver(receiver)
                .build();

        friendRequestRepository.save(newRequest);

        sender.getSentFriendRequests().add(newRequest);
        receiver.getReceivedFriendRequests().add(newRequest);
        receiver.getFollowers().add(sender);
        sender.getSubscriptions().add(receiver);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    //ОБработать случаи когда уже принял запрос. если статус ACCEPT брось эксепшн
    @Override
    @Transactional
    public void acceptRequest(String authorization, Long requestId) {
        UserEntity receiver = findUserEntityByIdFromToken(authorization);

        FriendRequestEntity request = findRequestByIdFromUser(requestId, receiver);

        if (!request.getRequestStatus().equals(RequestStatus.PENDING)) {
            throw new RejectedException("You have already answered this request");
        }

        UserEntity sender = request.getSender();

        request.setRequestStatus(RequestStatus.ACCEPTED);
        receiver.getFriends().add(sender);
        receiver.getSubscriptions().add(request.getSender());
        receiver.getFollowers().add(sender);

        sender.getFriends().add(receiver);
        sender.getFollowers().add(receiver);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    @Override
    @Transactional
    public void rejectRequest(String authorization, Long requestId) {
        UserEntity receiver = findUserEntityByIdFromToken(authorization);

        FriendRequestEntity request = findRequestByIdFromUser(requestId, receiver);

        if (!request.getRequestStatus().equals(RequestStatus.PENDING)) {
            throw new RejectedException("You have already answered this request");
        }
        request.setRequestStatus(RequestStatus.REJECTED);

        friendRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void unsubscribe(String authorization, Long requestId) {
        UserEntity user = findUserEntityByIdFromToken(authorization);

        FriendRequestEntity request =
                findRequestByIdFromUser(requestId, user);
        request.getSender().getSentFriendRequests().remove(request);
        user.getFriends().remove(request.getReceiver());
        user.getFollowers().remove(request.getReceiver());

        userRepository.save(user);
        friendRequestRepository.save(request);
    }

    //Ищу запрос у юзера , если нет - ошибка
    private FriendRequestEntity findRequestByIdFromUser(Long requestId, UserEntity receiver) {
        return receiver.getReceivedFriendRequests().stream()
                .filter(req -> req.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new NotFoundModelException("Request not found"));
    }

    //Ищу запрос у отправителя по айдишнику получателя
    private FriendRequestEntity findSenderRequestByReceiver(UserEntity sender, UserEntity receiver) {
        return sender.getSentFriendRequests().stream()
                .filter(req -> req.getReceiver().getId().equals(receiver.getId()))
                .findFirst()
                .orElse(null);
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

    public List<String> getSubscribers(String auth) {
        UserEntity userEntityByIdFromToken = findUserEntityByIdFromToken(auth);

        return userEntityByIdFromToken.getSubscriptions()
                .stream()
                .map(UserEntity::getUsername)
                .collect(Collectors.toList());
    }
//
//    private FriendRequestEntity findRequestById(Long id) {
//        return friendRequestRepository.findById(id).orElseThrow(
//                () -> new NotFoundModelException("AAA !!!!!!")
//        );
//    }
}
