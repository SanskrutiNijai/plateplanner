import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    mode: "light",
    // Primary: A fresh, vibrant green for main actions (like the logo)
    primary: {
      main: "#4CAF50", // Fresh Green
      light: "#81C784",
      dark: "#388E3C",
    },
    // Secondary: A warm accent color, perhaps a tomato red/orange
    secondary: {
      main: "#FF9800", // Orange/Amber
      light: "#FFB74D",
      dark: "#F57C00",
    },
    background: {
      default: "#F5F5F5", // Light background for a clean look
      paper: "#FFFFFF",
    },
  },
  typography: {
    fontFamily: "Roboto, Arial, sans-serif",
    h1: {
      fontWeight: 700,
      fontSize: "3.5rem",
    },
    h4: {
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 8, // Slightly rounded for a modern feel
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: "none", // Keep text readable
          fontWeight: 600,
        },
        containedPrimary: {
          // Bolder contrast for the main Login button
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
          "&:hover": {
            boxShadow: "0 6px 8px rgba(0, 0, 0, 0.15)",
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          transition: "transform 0.2s, box-shadow 0.2s",
          "&:hover": {
            transform: "translateY(-2px)",
            boxShadow: "0 4px 20px rgba(0, 0, 0, 0.08)",
          },
        },
      },
    },
  },
});

export default theme;