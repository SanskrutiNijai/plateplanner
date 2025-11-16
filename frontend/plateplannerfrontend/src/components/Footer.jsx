import { Box, Typography, Container, Grid, Link } from "@mui/material";

export default function Footer() {
  return (
    <Box
      sx={{
        width: "100%",
        bgcolor: "#333", // Dark background for contrast
        color: "white",
        py: 4,
        mt: 4,
      }}
    >
      <Container maxWidth="xl">
        <Grid container spacing={4}>
          <Grid item xs={12} sm={4}>
            <Typography variant="h6" gutterBottom sx={{ color: "primary.light" }}>
              PlatePlanner
            </Typography>
            <Typography variant="body2" color="inherit">
              Your personalized solution for managing, viewing, and getting smart recommendations for all your favorite recipes.
            </Typography>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Typography variant="h6" gutterBottom sx={{ color: "primary.light" }}>
              Features
            </Typography>
            <Box>
              <Typography sx={{ display: 'block', mb: 0.5 }}>Add/Edit Recipes</Typography>
              <Typography sx={{ display: 'block', mb: 0.5 }}>AI Recommendations</Typography>
              <Typography sx={{ display: 'block', mb: 0.5 }}>Premium Access</Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Typography variant="h6" gutterBottom sx={{ color: "primary.light" }}>
              Contact
            </Typography>
            <Typography variant="body2" color="inherit">
              Email: support@plateplanner.com
            </Typography>
            <Typography variant="body2" color="inherit">
              © {new Date().getFullYear()} PlatePlanner
            </Typography>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}