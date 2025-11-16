package com.plateplanner.userservice.controller;

import com.plateplanner.userservice.model.User;
import com.plateplanner.userservice.repository.UserRepository;
import com.plateplanner.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal Jwt jwt){
        // ⬇️ NEW: make sure the user exists in Mongo with premium=false by default
        userService.ensureUser(jwt);

        String username = jwt.getClaim("preferred_username");
        return "Hello, " + username; // ✅ functionality unchanged
    }

    @GetMapping("/users/me")
    public User me(@AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getClaimAsString("sub");
        return userRepository.findByKeycloakId(sub).orElseThrow();
    }
}
