package com.plateplanner.recipeintelligenceservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    private String id;

    // Keep both fields optional to coexist with existing docs from aiservice
    private String imageId;   // used by this service
    private String recipeId;  // used by aiservice (may be null here)

    private String userId;

    private String analysis;
    private List<String> improvements;
    private List<String> suggestions;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

