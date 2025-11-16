package com.plateplanner.userservice.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String keycloakId;          // from JWT "sub"
    private String username;            // from JWT "preferred_username"
    private String email;               // from JWT "email"

    private String firstName;           // from JWT "given_name"
    private String lastName;            // from JWT "family_name"

    private boolean premium;            // defaults to false on first create

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

