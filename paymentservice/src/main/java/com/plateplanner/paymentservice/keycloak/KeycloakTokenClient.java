package com.plateplanner.paymentservice.keycloak;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KeycloakTokenClient {

    @Value("${keycloak.base-url}")     private String baseUrl;
    @Value("${keycloak.realm}")        private String realm;
    @Value("${keycloak.client-id}")    private String clientId;
    @Value("${keycloak.client-secret}")private String clientSecret;

    private final RestTemplate rest = new RestTemplate();

    public String getAccessToken() {
        String url = baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;

        var res = rest.postForEntity(url, new HttpEntity<>(body, h), String.class);
        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
            throw new RuntimeException("Failed to get Keycloak token: " + res.getStatusCode());
        }
        return new JSONObject(res.getBody()).getString("access_token");
    }
}
