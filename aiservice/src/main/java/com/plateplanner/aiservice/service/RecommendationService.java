package com.plateplanner.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plateplanner.aiservice.model.Recipe;
import com.plateplanner.aiservice.model.Recommendation;
import com.plateplanner.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

 //   private final RecommendationRepository recommendationRepository;
//    private final GeminiService geminiService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public Recommendation generateRecommendation(String recipeId, String userId, String recipeDetails) {
//        String aiResponse = geminiService.getRecommendation(recipeDetails);
//
//        String analysis = null;
//        List<String> improvements = new ArrayList<>();
//        List<String> suggestions = new ArrayList<>();
//
//        try {
//            JsonNode root = objectMapper.readTree(aiResponse);
//
//            // Extract analysis text from nested structure if present
//            JsonNode candidates = root.path("candidates");
//            if (candidates.isArray() && candidates.size() > 0) {
//                JsonNode first = candidates.get(0);
//                JsonNode contentParts = first.path("content").path("parts");
//                if (contentParts.isArray() && contentParts.size() > 0) {
//                    analysis = contentParts.get(0).path("text").asText();
//                }
//            }
//
//            // Extract improvements if available
//            JsonNode improvementsNode = root.path("improvements");
//            if (improvementsNode.isArray()) {
//                for (JsonNode node : improvementsNode) {
//                    improvements.add(node.asText());
//                }
//            }
//
//            // Extract suggestions if available
//            JsonNode suggestionsNode = root.path("suggestions");
//            if (suggestionsNode.isArray()) {
//                for (JsonNode node : suggestionsNode) {
//                    suggestions.add(node.asText());
//                }
//            }
//
//        } catch (Exception e) {
//            analysis = "Failed to parse AI response.";
//            improvements.add("Could not extract improvements from AI response.");
//            suggestions.add("Verify AI output format.");
//        }
//
//        Recommendation recommendation = Recommendation.builder()
//                .recipeId(recipeId)
//                .userId(userId)
//                .analysis(analysis)
//                .improvements(improvements)
//                .suggestions(suggestions)
//                .rawRecommendation(aiResponse)
//                .build();
//
//        return recommendationRepository.save(recommendation);
//    }

    private final RecommendationRepository recommendationRepository;
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Recipe recipe){
        String prompt = createPromptForRecipe(recipe);
        String aiResponse = geminiService.getRecommendation(prompt);
        log.info("RESPONSE FROM AI {} " , aiResponse);
        return processAIResponse(recipe, aiResponse);

    }

    private Recommendation processAIResponse(Recipe recipe, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .get("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "nutrition", "Nutrition:");
            addAnalysisSection(fullAnalysis, analysisNode, "difficulty", "Difficulty:");
            addAnalysisSection(fullAnalysis, analysisNode, "timeEfficiency", "Time Efficiency:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));

//            return recommendationRepository.save(Recommendation.builder()
//                    .recipeId(recipe.getId())
//                    .userId(recipe.getUserId())
//                    .analysis(fullAnalysis.toString().trim())
//                    .improvements(improvements)
//                    .suggestions(suggestions)
//                    .createdAt(LocalDateTime.now())
//                    .build());

            // ✅ Check if a recommendation already exists for this recipe and user
            Optional<Recommendation> existingRecommendation = recommendationRepository
                    .findByRecipeIdAndUserId(recipe.getId(), recipe.getUserId());

            Recommendation recommendation;
            if (existingRecommendation.isPresent()) {
                // Update existing recommendation
                recommendation = existingRecommendation.get();
                recommendation.setAnalysis(fullAnalysis.toString().replace("\n", " ").trim());
                recommendation.setImprovements(improvements);
                recommendation.setSuggestions(suggestions);
                recommendation.setCreatedAt(LocalDateTime.now());
            } else {
                // Create new recommendation
                recommendation = Recommendation.builder()
                        .recipeId(recipe.getId())
                        .userId(recipe.getUserId())
                        .analysis(fullAnalysis.toString().replace("\n", " ").trim())
                        .improvements(improvements)
                        .suggestions(suggestions)
                        .createdAt(LocalDateTime.now())
                        .build();
            }

            // ✅ Save and return
            return recommendationRepository.save(recommendation);



        } catch (Exception e) {
            e.printStackTrace();
            return createDefaultRecommendation(recipe);
        }

    }

    private Recommendation createDefaultRecommendation(Recipe recipe) {

        return Recommendation.builder()
                .recipeId(recipe.getId())
                .userId(recipe.getUserId())
                .analysis("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Consider enhancing the flavor balance or presentation to improve the overall dining experience."))
                .suggestions(Collections.singletonList("Try a similar recipe or complementary dish to expand your culinary variety and skills."))
                .createdAt(LocalDateTime.now())
                .build();

    }


    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {

        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String recommendation = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, recommendation));
            });
        }
        return improvements.isEmpty() ? Collections.singletonList("No specific improvements provided") : improvements;

    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String recipe = suggestion.path("recipe").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", recipe, description));
            });
        }
        return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions provided") : suggestions;
    }

    private String createPromptForRecipe(Recipe recipe) {

        return String.format("""
                You are a world-class culinary AI specializing in recipe analysis, nutrition optimization, and meal recommendations. 
                Your task is to deeply analyze the following recipe and generate an expert-level assessment.

                OUTPUT REQUIREMENTS:
                - Respond ONLY in valid JSON (no additional commentary).
                - Use the EXACT structure shown below.
                - All text values must be complete, natural-language sentences.
                - Each section should be specific, actionable, and personalized to the given recipe.

                JSON FORMAT:
                {
                    "analysis": {
                        "overall": "Comprehensive summary of the recipe’s flavor balance, appeal, and creativity.",
                        "nutrition": "Detailed nutritional evaluation (macronutrients, calories, balance, possible health considerations).",
                        "difficulty": "Assessment of the cooking complexity, required skills, and preparation challenges.",
                        "timeEfficiency": "Evaluation of whether the estimated time is appropriate and where optimizations can be made."
                    },
                    "improvements": [
                        {
                            "area": "Specific aspect to enhance (e.g., flavor, texture, nutrition, time efficiency).",
                            "recommendation": "Detailed, actionable improvement suggestion."
                        }
                    ],
                    "suggestions": [
                        {
                            "recipe": "Recommended related or complementary recipe.",
                            "description": "Explanation of why it’s recommended (e.g., similar flavor profile, dietary balance, pairing suggestion)."
                        }
                    ]
                }

                RECIPE TO ANALYZE:
                Recipe Name: %s
                Type: %s
                Ingredients: %s
                Instructions: %s
                Estimated Time: %d minutes

                TASK:
                - Provide a rich culinary analysis focusing on flavor harmony, nutritional balance, and preparation efficiency.
                - Suggest concrete improvements (ingredient adjustments, cooking technique changes, or time optimizations).
                - Recommend related or complementary recipes that fit the user’s likely preferences or goals (e.g., healthier variant, side dish, similar cuisine).
                - Avoid generic responses — make it personalized to the given recipe details.

                Ensure your response is 100%% valid JSON and follows the structure exactly as shown above.
                """,
                recipe.getName(),
                recipe.getType(),
                String.join(", ", recipe.getIngredients()),
                recipe.getInstructions(),
                recipe.getEstimatedTime()

        );

    }

    public List<Recommendation> getUserRecommendations(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation getRecommendationByRecipe(String recipeId) {
        return recommendationRepository.findByRecipeId(recipeId)
                .orElseThrow(() -> new RuntimeException("No recommendation found for this recipe: " + recipeId));
    }
}
