package com.plateplanner.recipeservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "recipes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    @Id
    private String id;

    private String userId;  // Keycloak user ID
    private String name;
    private String type; // breakfast, lunch, dinner, snacks, dessert
    private List<String> ingredients;
    private String instructions;
    private Integer estimatedTime; // in minutes

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
