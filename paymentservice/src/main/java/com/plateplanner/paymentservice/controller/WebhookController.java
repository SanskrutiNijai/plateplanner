package com.plateplanner.paymentservice.controller;

import com.plateplanner.paymentservice.service.WebhookUpgradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookUpgradeService webhookService;

    @PostMapping(value = "/webhook", consumes = "application/json")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature,
            @RequestBody String rawBody) {
        try {
            webhookService.processWebhook(rawBody, signature);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid webhook");
        }
    }
}
