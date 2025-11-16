package com.plateplanner.aiservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recommendations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {

    @Id
    private String id;

    private String recipeId; // The recipe this recommendation belongs to
    private String userId;   // Keycloak user ID

    private String analysis; // The main explanation or analysis text
    private List<String> improvements;
    private List<String> suggestions;

    //private String rawRecommendation; // optional - store full AI JSON

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
