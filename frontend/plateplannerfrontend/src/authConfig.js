export const authConfig = {
    clientId: import.meta.env.VITE_AUTH_CLIENT_ID,
    authorizationEndpoint: import.meta.env.VITE_AUTH_ENDPOINT,
    tokenEndpoint: import.meta.env.VITE_TOKEN_ENDPOINT,
    logoutEndpoint: import.meta.env.VITE_LOGOUT_ENDPOINT,
    redirectUri: import.meta.env.VITE_REDIRECT_URI, // NOTE: Update this for production URL
    scope: import.meta.env.VITE_SCOPE,
    onRefreshTokenExpire: (event) => event.logIn(),
};