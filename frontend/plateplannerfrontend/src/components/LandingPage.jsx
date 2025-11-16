import { Box, Typography, Button, Container, Grid } from "@mui/material";
import KitchenIcon from '@mui/icons-material/Kitchen';
import RecommendIcon from '@mui/icons-material/Recommend';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PhotoCameraIcon from "@mui/icons-material/PhotoCamera";


export default function LandingPage({ logIn }) {
  return (
    <Container maxWidth="xl" sx={{ mt: 5, mb: 10 }}>
      {/* Hero Section - Inspired by the reference image banner */}
      <Box
        sx={{
          bgcolor: "#E8F5E9", // Very light green background
          borderRadius: 4,
          p: { xs: 3, md: 8 },
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          alignItems: "center",
          minHeight: "450px",
          overflow: "hidden",
        }}
      >
        {/* Text Content */}
        <Box sx={{ flex: 1, pr: { md: 5 }, mb: { xs: 4, md: 0 } }}>
          <Typography
            variant="h1"
            sx={{
              color: "primary.dark",
              mb: 2,
              fontWeight: 800,
            }}
          >
            🍽️ PlatePlanner:
            <br />
            Your Recipe Companion
          </Typography>
          <Typography variant="h5" color="text.secondary" sx={{ mb: 4, maxWidth: 500 }}>
            Effortlessly Add, Update, Delete, and View your favorite recipes.
          </Typography>
          <Typography variant="h5" color="text.secondary" sx={{ mb: 4, maxWidth: 500 }}>
            Get Smart Recommendations powered by AI!
          </Typography>
          <Button
            variant="contained"
            color="secondary" // Use secondary for the main action button
            size="large"
            onClick={() => logIn()} // ✅ FIXED: Prevents passing the event object
            sx={{ py: 1.5, px: 4, fontSize: '1.2rem' }}
          >
            Start Cooking – Register Now
          </Button>
        </Box>

        {/* Image/Visual Mockup */}
        <Box
          sx={{
            flex: 1,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            position: 'relative',
          }}
        >
          <Box
            sx={{
              width: { xs: '80%', md: '500px' },
              height: { xs: '200px', md: '500px' },
              borderRadius: '50%',
              bgcolor: 'primary.light',
              position: 'absolute',
              opacity: 0.3,
              zIndex: 0,
            }}
          />
          <Typography
            variant="h2"
            sx={{
              position: 'relative',
              zIndex: 1,
              color: 'primary.dark',
              opacity: 0.8,
              fontSize: '8rem',
            }}
          >
            🥘
          </Typography>
          
        </Box>
      </Box>
      
      {/* Feature Section */}
   <Box sx={{ mt: 10, textAlign: "center" }}>
  <Typography variant="h4" sx={{ fontWeight: 700, mb: 5 }}>
    Why PlatePlanner?
  </Typography>

  <Grid container spacing={4} justifyContent="center">
    <Grid item xs={12} md={6}>
      <KitchenIcon color="primary" sx={{ fontSize: 55, mb: 1 }} />
      <Typography variant="h6" sx={{ fontWeight: 600 }}>
        Centralized Recipe Hub
      </Typography>
      <Typography variant="body2" color="text.secondary">
        Store and manage all your recipes in one place.
      </Typography>
    </Grid>

    <Grid item xs={12} md={6}>
      <RecommendIcon color="secondary" sx={{ fontSize: 55, mb: 1 }} />
      <Typography variant="h6" sx={{ fontWeight: 600 }}>
        Smart AI Recommendations
      </Typography>
      <Typography variant="body2" color="text.secondary">
        Get instant suggestions to improve taste & nutrition.
      </Typography>
    </Grid>

    <Grid item xs={12} md={6}>
      <LocalShippingIcon color="primary" sx={{ fontSize: 55, mb: 1 }} />
      <Typography variant="h6" sx={{ fontWeight: 600 }}>
        Seamless Integration
      </Typography>
      <Typography variant="body2" color="text.secondary">
        Secure login via Keycloak with easy premium upgrade.
      </Typography>
    </Grid>

    <Grid item xs={12} md={6}>
      <PhotoCameraIcon color="secondary" sx={{ fontSize: 55, mb: 1 }} />
      <Typography variant="h6" sx={{ fontWeight: 600 }}>
        AI Image Recipe Reader
      </Typography>
      <Typography variant="body2" color="text.secondary">
        Upload recipe photos — AI extracts text & ingredients.
      </Typography>
    </Grid>
  </Grid>
</Box>

    </Container>
  );
}