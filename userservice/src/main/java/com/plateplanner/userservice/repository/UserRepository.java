package com.plateplanner.userservice.repository;

import com.plateplanner.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByKeycloakId(String keycloakId);
    boolean existsByKeycloakId(String keycloakId);
}

