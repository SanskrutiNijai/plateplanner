package com.plateplanner.aiservice.controller;

import com.plateplanner.aiservice.model.Recipe;
import com.plateplanner.aiservice.model.Recommendation;
import com.plateplanner.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final WebClient.Builder webClientBuilder; // Inject WebClient builder

    @Value("${recipeservice.url}")
    private String recipeServiceUrl;

    @PostMapping("/generate/{recipeId}")
    public ResponseEntity<Recommendation> generateRecommendationById(
            @PathVariable String recipeId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // Build WebClient
        WebClient webClient = webClientBuilder.build();

        // Fetch the recipe from Recipe microservice
        Recipe recipe = webClient.get()
                .uri(recipeServiceUrl + "/recipes/" + recipeId)
                .header("Authorization", "Bearer " + jwt.getTokenValue()) // JWT token
                .retrieve()
                .bodyToMono(Recipe.class)
                .block();

        if (recipe == null) {
            throw new RuntimeException("Recipe not found: " + recipeId);
        }

        // Set userId from JWT
        recipe.setUserId(jwt.getClaimAsString("sub"));

        // Generate AI recommendation
        Recommendation rec = recommendationService.generateRecommendation(recipe);

        return ResponseEntity.ok(rec);
    }

    @GetMapping("/recipe/user")
    public ResponseEntity<List<Recommendation>> getUserRecommendations(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(recommendationService.getUserRecommendations(jwt.getClaimAsString("sub")));
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<Recommendation> getRecommendationByRecipe(@PathVariable String recipeId) {
        return ResponseEntity.ok(recommendationService.getRecommendationByRecipe(recipeId));
    }
}
