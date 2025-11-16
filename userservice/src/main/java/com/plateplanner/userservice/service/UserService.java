package com.plateplanner.userservice.service;

import com.plateplanner.userservice.model.User;
import com.plateplanner.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    /** Ensure a user document exists; create with premium=false if first time. */
    public void ensureUser(Jwt jwt) {
        String sub = jwt.getClaimAsString("sub");
        if (repo.existsByKeycloakId(sub)) return;

        try {
            User user = User.builder()
                    .keycloakId(sub)
                    .username(jwt.getClaimAsString("preferred_username"))
                    .email(jwt.getClaimAsString("email"))
                    .firstName(jwt.getClaimAsString("given_name"))
                    .lastName(jwt.getClaimAsString("family_name"))
                    .premium(false)
                    .build();
            repo.save(user);
        } catch (Exception e) {
            // TEMP: see write/index errors clearly
            e.printStackTrace();
            throw e;
        }
    }
}

