package com.plateplanner.recipeservice.controller;

import com.plateplanner.recipeservice.model.Recipe;
import com.plateplanner.recipeservice.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/add")
    public ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe, @AuthenticationPrincipal Jwt jwt){
        recipe.setUserId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(recipeService.addRecipe(recipe));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Recipe>> getUserRecipes(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(recipeService.getUserRecipes(jwt.getClaimAsString("sub")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id){
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe recipe){
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable String id){
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok("Recipe deleted successfully");
    }
}

