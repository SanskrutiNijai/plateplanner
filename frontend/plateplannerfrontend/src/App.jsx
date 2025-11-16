import { useContext, useEffect, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ThemeProvider, CssBaseline, Box } from "@mui/material";
import theme from "./theme";

import Header from "./components/Header";
import Footer from "./components/Footer";
import LandingPage from "./components/LandingPage";

import Dashboard from "./pages/Dashboard";
import PremiumDashboard from "./pages/PremiumDashboard";
import { getCurrentUser } from "./api/recipeApi";

function App() {
  const { token, logIn, logOut } = useContext(AuthContext);
  const isAuthenticated = !!token;

  const [premium, setPremium] = useState(false);

  useEffect(() => {
    let cancelled = false;
    async function fetchMe() {
      if (!token) {
        setPremium(false);
        return;
      }
      try {
        const me = await getCurrentUser(token);
        if (!cancelled) setPremium(Boolean(me.data?.premium));
      } catch {
        if (!cancelled) setPremium(false);
      }
    }
    fetchMe();
    return () => { cancelled = true; };
  }, [token]);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Box sx={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
          <Header isAuthenticated={isAuthenticated} logIn={logIn} logOut={logOut} premium={premium} />

          <Box sx={{ flex: 1, width: "100%" }}>
            {isAuthenticated ? (
              <Routes>
                <Route path="/" element={<Dashboard token={token} premium={premium} setPremium={setPremium} />} />
                <Route path="/premium" element={<PremiumDashboard premium={premium} />} />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            ) : (
              <LandingPage logIn={logIn} />
            )}
          </Box>

          <Footer />
        </Box>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
