package com.plateplanner.paymentservice.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakRoleService {

    @Value("${keycloak.base-url}")     private String baseUrl;
    @Value("${keycloak.realm}")        private String realm;
    @Value("${keycloak.premium.role}") private String premiumRole;

    private final KeycloakTokenClient tokenClient;
    private final RestTemplate rest = new RestTemplate();

    /**
     * Attempt to assign the configured realm role to a user.
     * This method is best-effort: it logs and swallows 403/other client errors
     * so webhook processing can continue (DB already flipped premium=true).
     */
    public void assignPremiumRole(String keycloakUserId) {
        String token = tokenClient.getAccessToken();

        // build URLs
        String roleUrl = baseUrl + "/admin/realms/" + realm + "/roles/" + premiumRole;
        String assignUrl = baseUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId + "/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        try {
            log.info("KeycloakRoleService: fetching role representation from {}", roleUrl);

            ResponseEntity<String> roleRes = rest.exchange(roleUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (!roleRes.getStatusCode().is2xxSuccessful() || roleRes.getBody() == null) {
                log.warn("Keycloak role fetch returned non-2xx: {} - body: {}", roleRes.getStatusCode(), roleRes.getBody());
                return;
            }

            JSONObject role = new JSONObject(roleRes.getBody());

            log.info("KeycloakRoleService: assigning role '{}' to user {}", premiumRole, keycloakUserId);
            headers.setContentType(MediaType.APPLICATION_JSON);
            JSONArray body = new JSONArray().put(role);

            ResponseEntity<String> assignRes = rest.exchange(assignUrl, HttpMethod.POST, new HttpEntity<>(body.toString(), headers), String.class);
            log.info("KeycloakRoleService: role assignment response: {} {}", assignRes.getStatusCode(), assignRes.getBody());

        } catch (HttpClientErrorException.Forbidden fe) {
            // 403: token doesn't have permission to call admin API
            log.warn("Keycloak admin API returned 403 Forbidden when assigning role. " +
                    "This usually means the service-account lacks realm-management roles. " +
                    "Role URL: {}. Response: {}", roleUrl, fe.getResponseBodyAsString());
            // swallow: DB update is already done; role assign is optional
        } catch (HttpClientErrorException he) {
            // Other 4xx/5xx with response body
            log.error("Keycloak admin API returned error {}. Response: {}", he.getStatusCode(), he.getResponseBodyAsString(), he);
        } catch (Exception e) {
            // Unexpected error: log and continue
            log.error("Failed to assign premium role to Keycloak user {}: {}", keycloakUserId, e.getMessage(), e);
        }
    }
}
