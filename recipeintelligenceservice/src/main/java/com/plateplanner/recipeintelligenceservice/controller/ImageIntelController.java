package com.plateplanner.recipeintelligenceservice.controller;


import com.mongodb.client.gridfs.model.GridFSFile;
import com.plateplanner.recipeintelligenceservice.dto.RecommendResponse;
import com.plateplanner.recipeintelligenceservice.dto.UploadResponse;
import com.plateplanner.recipeintelligenceservice.model.Recommendation;
import com.plateplanner.recipeintelligenceservice.service.GeminiService;
import com.plateplanner.recipeintelligenceservice.service.ImageStorageService;
import com.plateplanner.recipeintelligenceservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class ImageIntelController {

    private final ImageStorageService imageStorageService;
    private final GeminiService geminiService;
    private final RecommendationService recommendationService;

    @PostMapping("/images")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file,
                                                 @AuthenticationPrincipal Jwt jwt) throws IOException {
        String userId = jwt.getClaimAsString("sub");
        ObjectId id = imageStorageService.store(file, userId);

        String fileId = id.toHexString();
        return ResponseEntity.ok(new UploadResponse(
                fileId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                "/api/ri/images/" + fileId + "/view",
                "/api/ri/images/" + fileId + "/download"
        ));
    }

    @GetMapping("/images/{id}/view")
    public ResponseEntity<InputStreamResource> view(@PathVariable String id,
                                                    @AuthenticationPrincipal Jwt jwt) throws IOException {
        String userId = jwt.getClaimAsString("sub");
        GridFSFile file = imageStorageService.getFile(id);
        imageStorageService.assertOwner(file, userId);
        GridFsResource resource = imageStorageService.getResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageStorageService.contentType(file)))
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @GetMapping("/images/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id,
                                                        @AuthenticationPrincipal Jwt jwt) throws IOException {
        String userId = jwt.getClaimAsString("sub");
        GridFSFile file = imageStorageService.getFile(id);
        imageStorageService.assertOwner(file, userId);
        GridFsResource resource = imageStorageService.getResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(imageStorageService.filename(file)).build());
        headers.setContentType(MediaType.parseMediaType(imageStorageService.contentType(file)));

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(resource.getInputStream()));
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");

        // delete image (ensures ownership)
        imageStorageService.delete(id, userId);

        // also delete any recommendation tied to this image for this user
        recommendationService.deleteByImageAndUser(id, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/recommend/{imageId}")
    public ResponseEntity<RecommendResponse> recommend(@PathVariable String imageId,
                                                       @AuthenticationPrincipal Jwt jwt) throws IOException {
        String userId = jwt.getClaimAsString("sub");

        // Verify ownership before reading
        GridFSFile file = imageStorageService.getFile(imageId);
        imageStorageService.assertOwner(file, userId);

        // Read bytes
        byte[] bytes = imageStorageService.loadBytes(imageId);
        String contentType = imageStorageService.contentType(file);

        // One-shot Gemini
        var parsed = geminiService.analyzeImage(bytes, contentType);

        // Save (upsert) recommendation in the SAME 'recommendations' collection
        Recommendation saved = recommendationService.upsertImageRecommendation(
                imageId, userId, parsed.analysis(), parsed.improvements(), parsed.suggestions()
        );

        return ResponseEntity.ok(
                RecommendResponse.builder()
                        .id(saved.getId())
                        .imageId(saved.getImageId())
                        .recipeId(saved.getRecipeId()) // likely null for image-based recs
                        .userId(saved.getUserId())
                        .analysis(saved.getAnalysis())
                        .improvements(saved.getImprovements())
                        .suggestions(saved.getSuggestions())
                        .createdAt(saved.getCreatedAt())
                        .updatedAt(saved.getUpdatedAt())
                        .build()
        );
    }

    @GetMapping("/recommendations/user")
    public ResponseEntity<?> getMyRecommendations(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(recommendationService.getUserRecommendations(userId));
    }

    @GetMapping("/recommendations/{imageId}")
    public ResponseEntity<?> getByImage(@PathVariable String imageId,
                                        @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        Recommendation rec = recommendationService.getByImageId(imageId);
        if (!rec.getUserId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "You do not own this recommendation");
        }
        return ResponseEntity.ok(rec);
    }


    @GetMapping("/images")
    public ResponseEntity<?> listMyImages(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");

        var files = imageStorageService.findAllByUser(userId);

        var list = files.stream().map(f -> {
            String id = f.getObjectId().toHexString();
            return Map.of(
                    "id", id,
                    "filename", f.getFilename(),
                    "contentType", f.getMetadata().getString("contentType"),
                    "size", f.getLength(),
                    "viewUrl", "/api/ri/images/" + id + "/view",
                    "downloadUrl", "/api/ri/images/" + id + "/download"
            );
        }).toList();

        return ResponseEntity.ok(list);
    }


    // Optional: localized error for malformed ids from path variables
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badId(IllegalArgumentException ex) {
        return ResponseEntity.status(BAD_REQUEST).body("Invalid id");
    }


}

