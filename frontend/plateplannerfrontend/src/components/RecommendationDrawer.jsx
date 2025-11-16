import { Drawer, Box, Typography, Divider, List, ListItem, useMediaQuery, useTheme } from "@mui/material";

export default function RecommendationDrawer({ open, onClose, recommendation }) {
  if (!recommendation) return null;

  const theme = useTheme();
  // Check if the screen is small (less than 600px wide)
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm')); 

  return (
    <Drawer anchor="right" open={open} onClose={onClose}>
      <Box 
        sx={{ 
          // Set width based on screen size: 
          // 95% width on small screens, fixed 600px on larger screens
          width: isSmallScreen ? '95vw' : 600, 
          maxWidth: '100vw', // Ensure it never exceeds the screen width
          p: 3 
        }}
      >
        <Typography variant="h6">AI Recommendation</Typography>
        <Typography variant="body2" sx={{ mt: 1 }}>{recommendation.analysis}</Typography>

        <Divider sx={{ my: 2 }} />
        <Typography variant="subtitle1">Improvements:</Typography>
        <List>
          {recommendation.improvements?.map((i, idx) => (
            <ListItem key={idx}>• {i}</ListItem>
          ))}
        </List>

        <Typography variant="subtitle1">Suggestions:</Typography>
        <List>
          {recommendation.suggestions?.map((s, idx) => (
            <ListItem key={idx}>• {s}</ListItem>
          ))}
        </List>
      </Box>
    </Drawer>
  );
}