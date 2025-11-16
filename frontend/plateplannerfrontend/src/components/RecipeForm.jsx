import { useState } from "react";
import { Box, Button, TextField, MenuItem, Typography, Divider } from "@mui/material"; // Added Typography, Divider

export default function RecipeForm({ onSubmit, initialData, onCancel }) {
  const [recipe, setRecipe] = useState(
    initialData || {
      name: "",
      type: "",
      ingredients: "",
      instructions: "",
      estimatedTime: "",
    }
  );

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRecipe({ ...recipe, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const data = {
      ...recipe,
      // Functionality preserved: splitting ingredients string into an array
      ingredients: recipe.ingredients.split(",").map((i) => i.trim()),
    };
    onSubmit(data);
  };

  return (
    <Box>
      <Typography variant="h5" gutterBottom color="primary.dark">
        {initialData ? "Edit Recipe" : "Add New Recipe"}
      </Typography>
      <Divider sx={{ mb: 3 }} />
      <Box component="form" onSubmit={handleSubmit} sx={{ display: "grid", gap: 3 }}>
        <TextField label="Recipe Name" name="name" value={recipe.name} onChange={handleChange} required fullWidth />
        <TextField
          select
          label="Type"
          name="type"
          value={recipe.type}
          onChange={handleChange}
          required
          fullWidth
        >
          {["breakfast", "lunch", "dinner"].map((option) => (
            <MenuItem key={option} value={option}>
              {option.charAt(0).toUpperCase() + option.slice(1)}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          label="Ingredients (comma separated, e.g., 'flour, eggs, milk')"
          name="ingredients"
          value={recipe.ingredients}
          onChange={handleChange}
          multiline
          rows={3}
          required
          fullWidth
        />
        <TextField
          label="Instructions"
          name="instructions"
          value={recipe.instructions}
          onChange={handleChange}
          multiline
          rows={5}
          required
          fullWidth
        />
        <TextField
          label="Estimated Time (minutes)"
          name="estimatedTime"
          type="number"
          value={recipe.estimatedTime}
          onChange={handleChange}
          required
          fullWidth
        />
        <Box display="flex" gap={2} mt={2} justifyContent="flex-end">
          <Button onClick={onCancel} variant="outlined" color="error">
            Cancel
          </Button>
          <Button type="submit" variant="contained" color="primary">
            {initialData ? "Update Recipe" : "Add Recipe"}
          </Button>
        </Box>
      </Box>
    </Box>
  );
}