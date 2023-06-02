package by.karpovich.SocialMedia.api.controller;

import by.karpovich.SocialMedia.api.dto.authentification.JwtResponse;
import by.karpovich.SocialMedia.api.dto.authentification.LoginForm;
import by.karpovich.SocialMedia.api.dto.authentification.RegistrationForm;
import by.karpovich.SocialMedia.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Authentication API")
public class AuthController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/signin")
    public JwtResponse authenticateUser(@Valid @RequestBody LoginForm loginForm) {
        return userServiceImpl.signIn(loginForm);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationForm signUpRequest) {
        userServiceImpl.signUp(signUpRequest);

        return new ResponseEntity<>(String.format("%s registered successfully!", signUpRequest.getUsername()), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }
}