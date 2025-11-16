package com.plateplanner.recipeintelligenceservice.service;


import com.plateplanner.recipeintelligenceservice.model.Recommendation;
import com.plateplanner.recipeintelligenceservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;

    public Recommendation upsertImageRecommendation(String imageId, String userId,
                                                    String analysis, List<String> improvements, List<String> suggestions) {
        Optional<Recommendation> existing = repository.findByImageIdAndUserId(imageId, userId);
        Recommendation rec = existing.orElseGet(() -> Recommendation.builder()
                .imageId(imageId)
                .userId(userId)
                .build());

        rec.setAnalysis(analysis);
        rec.setImprovements(improvements);
        rec.setSuggestions(suggestions);
        rec.setUpdatedAt(LocalDateTime.now());

        return repository.save(rec);
    }

    public List<Recommendation> getUserRecommendations(String userId) {
        return repository.findByUserId(userId);
    }

    public Recommendation getByImageId(String imageId) {
        return repository.findByImageId(imageId)
                .orElseThrow(() -> new RuntimeException("No recommendation found for image: " + imageId));
    }

    public void deleteByImageAndUser(String imageId, String userId) {
        repository.deleteByImageIdAndUserId(imageId, userId);
    }
}

