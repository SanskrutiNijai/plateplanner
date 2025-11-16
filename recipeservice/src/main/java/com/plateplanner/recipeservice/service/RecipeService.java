package com.plateplanner.recipeservice.service;

import com.plateplanner.recipeservice.model.Recipe;
import com.plateplanner.recipeservice.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public Recipe addRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public List<Recipe> getUserRecipes(String userId) {
        return recipeRepository.findByUserId(userId);
    }

    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));
    }

    public Recipe updateRecipe(String id, Recipe updatedRecipe) {
        Recipe recipe = getRecipeById(id);
        recipe.setName(updatedRecipe.getName());
        recipe.setType(updatedRecipe.getType());
        recipe.setIngredients(updatedRecipe.getIngredients());
        recipe.setInstructions(updatedRecipe.getInstructions());
        recipe.setEstimatedTime(updatedRecipe.getEstimatedTime());
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(String id) {
        recipeRepository.deleteById(id);
    }
}
