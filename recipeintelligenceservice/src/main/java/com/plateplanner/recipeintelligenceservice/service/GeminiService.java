package com.plateplanner.recipeintelligenceservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gemini.api.url}")
    private String geminiApiUrl;   // e.g. https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private WebClient client() {
        return WebClient.builder().build();
    }

    /**
     * ONE multimodal request: send the image + instruction, expect strict JSON back.
     */
    public ParsedRecommendation analyzeImage(byte[] imageBytes, String mimeType) {
        try {
            // build absolute URI with host + query param
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(geminiApiUrl)     // ✅ build from FULL URL, not .path(...)
                    .queryParam("key", geminiApiKey)
                    .build(true)                   // keep encoded if any
                    .toUri();

            log.info("Calling Gemini at: {}", uri);

            String base64 = Base64.encodeBase64String(imageBytes);

            Map<String, Object> inlineData = Map.of(
                    "mimeType", (mimeType != null && !mimeType.isBlank()) ? mimeType : "image/jpeg",
                    "data", base64
            );

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(
                                    Map.of("text",
                                            "Extract any recipe text from this image and return ONLY strict JSON with: " +
                                                    "{ \"analysis\": string, \"improvements\": string[], \"suggestions\": string[] }. " +
                                                    "No markdown fences. No extra keys."
                                    ),
                                    Map.of("inline_data", inlineData)
                            )
                    ))
            );

            String response = client().post()
                    .uri(uri)                                  // ✅ proper absolute URI
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res ->
                            res.bodyToMono(String.class)
                                    .map(body -> new ResponseStatusException(BAD_GATEWAY,
                                            "Gemini API error (" + res.statusCode() + "): " + body))
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("Gemini raw: {}", response);

            JsonNode root = mapper.readTree(response);
            JsonNode parts = root.path("candidates").get(0).path("content").path("parts");
            if (!parts.isArray() || parts.size() == 0) {
                throw new ResponseStatusException(BAD_GATEWAY, "Invalid response from AI");
            }

            // Some models wrap JSON in code fences; strip if present
            String json = parts.get(0).path("text").asText()
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode payload = mapper.readTree(json);

            String analysis = asTextOr(payload, "analysis", "No analysis generated.");
            List<String> improvements = toStringList(payload.path("improvements"), "No specific improvements provided.");
            List<String> suggestions  = toStringList(payload.path("suggestions"),  "No specific suggestions provided.");

            return new ParsedRecommendation(analysis, improvements, suggestions);

        } catch (ResponseStatusException e) {
            throw e; // bubble up with the status we set above
        } catch (Exception e) {
            log.error("Gemini analyzeImage failed", e);
            // graceful fallback so your endpoint still returns something useful
            return new ParsedRecommendation(
                    "Unable to generate detailed analysis.",
                    List.of("Try retaking the photo with better lighting and clarity."),
                    List.of("Consider uploading a clearer image or typing the recipe.")
            );
        }
    }

    private String asTextOr(JsonNode node, String field, String fallback) {
        JsonNode v = node.path(field);
        return (v != null && v.isTextual()) ? v.asText() : fallback;
    }

    private List<String> toStringList(JsonNode arr, String fallback) {
        if (arr != null && arr.isArray() && arr.size() > 0) {
            List<String> list = new ArrayList<>();
            arr.forEach(x -> list.add(x.asText()));
            return list;
        }
        return List.of(fallback);
    }

    // compact DTO for internal use
    public record ParsedRecommendation(String analysis, List<String> improvements, List<String> suggestions) {}
}
