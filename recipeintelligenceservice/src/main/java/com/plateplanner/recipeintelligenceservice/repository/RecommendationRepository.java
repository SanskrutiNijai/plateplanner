package com.plateplanner.recipeintelligenceservice.repository;

import com.plateplanner.recipeintelligenceservice.model.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
    List<Recommendation> findByUserId(String userId);
    Optional<Recommendation> findByImageId(String imageId);
    Optional<Recommendation> findByImageIdAndUserId(String imageId, String userId);
    void deleteByImageIdAndUserId(String imageId, String userId);

    // (already used by your aiservice; harmless to include)
    Optional<Recommendation> findByRecipeIdAndUserId(String recipeId, String userId);


}

