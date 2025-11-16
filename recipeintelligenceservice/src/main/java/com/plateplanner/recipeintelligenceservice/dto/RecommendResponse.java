package com.plateplanner.recipeintelligenceservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecommendResponse {
    private String id;
    private String imageId;          // for image-based recs
    private String recipeId;         // may be null for image-based recs (compatible with existing docs)
    private String userId;
    private String analysis;
    private List<String> improvements;
    private List<String> suggestions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

