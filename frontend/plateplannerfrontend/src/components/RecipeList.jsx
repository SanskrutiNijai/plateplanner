import { Card, CardContent, CardActions, Typography, Button, Grid, Box } from "@mui/material";
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import RestaurantIcon from '@mui/icons-material/Restaurant';

export default function RecipeList({ recipes, onEdit, onDelete, onRecommend }) {
  if (recipes.length === 0) {
    return (
      <Typography variant="h6" color="text.secondary" align="center" sx={{ mt: 5 }}>
        No recipes found. Try adjusting your search or add a new one!
      </Typography>
    );
  }

  return (
    // Changed Grid size to allow more items per row on large screens
    <Grid container spacing={3} sx={{ width: '100%' }}>
      {recipes.map((r) => (
        // Changed size to better utilize space (3 items per row on medium/large)
        <Grid item xs={12} sm={6} md={4} key={r.id}> 
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <CardContent>
              <Typography variant="h5" component="div" sx={{ mb: 1, color: 'primary.main', fontWeight: 600 }}>
                {r.name}
              </Typography>
              <Typography color="text.secondary" sx={{ textTransform: 'capitalize', mb: 2, display: 'flex', alignItems: 'center' }}>
                <RestaurantIcon sx={{ fontSize: 16, mr: 0.5 }} /> {r.type}
              </Typography>
              
              <Typography variant="body2" sx={{ mt: 1, borderLeft: '3px solid #FF9800', pl: 1, bgcolor: '#fff3e0' }}>
                Ingredients: {r.ingredients.join(", ")}
              </Typography>
              
              <Typography variant="body2" sx={{ mt: 1 }}>
                Instructions: {r.instructions}
              </Typography>
              
              <Typography variant="body2" sx={{ mt: 2, color: 'text.primary', display: 'flex', alignItems: 'center' }}>
                <AccessTimeIcon fontSize="small" sx={{ mr: 0.5 }} /> Time: {r.estimatedTime} mins
              </Typography>
            </CardContent>
            <CardActions sx={{ pt: 0, justifyContent: 'space-between' }}>
              <Box>
                <Button size="small" onClick={() => onEdit(r)} variant="outlined">Edit</Button>
                <Button size="small" color="error" onClick={() => onDelete(r.id)} sx={{ ml: 1 }}>Delete</Button>
              </Box>
              <Button size="small" color="primary" variant="contained" onClick={() => onRecommend(r.id)}>
                AI Recommend
              </Button>
            </CardActions>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}