import { Box, Typography, Button, Container } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function Header({ isAuthenticated, logIn, logOut }) {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        width: "100%",
        bgcolor: "background.paper",
        py: 2,
        borderBottom: "1px solid #eee",
        position: "sticky",
        top: 0,
        zIndex: 1000,
      }}
    >
      <Container maxWidth="xl" sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Typography
            variant="h4"
            sx={{ fontWeight: 700, color: "primary.main", cursor: "pointer" }}
            onClick={() => navigate("/")}
          >
            🍽️ PlatePlanner
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ display: { xs: "none", sm: "block" } }}>
            The Recipe Manager
          </Typography>
        </Box>

        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          {/* {isAuthenticated && premium && (
            <Button variant="text" onClick={() => navigate("/premium")}>
              Premium Access
            </Button>
          )} */}
          {!isAuthenticated ? (
            <Button variant="contained" color="primary" onClick={() => logIn()} size="large">
              Login to Dashboard
            </Button>
          ) : (
            <Button variant="outlined" color="secondary" onClick={() => logOut()} size="medium">
              Logout
            </Button>
          )}
        </Box>
      </Container>
    </Box>
  );
}
