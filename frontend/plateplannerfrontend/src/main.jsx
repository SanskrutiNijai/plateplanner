import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { AuthProvider } from "react-oauth2-code-pkce";
import { authConfig } from "./authConfig.js";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <AuthProvider
      authConfig={authConfig}
      // ✅ These ensure proper login persistence and smooth reloads
      autoLogin={false}
      persist={true}
      loadingComponent={<div>Loading...</div>}
    >
      <App />
    </AuthProvider>
  </StrictMode>
);
