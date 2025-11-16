package com.plateplanner.paymentservice.controller;

import com.plateplanner.paymentservice.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    // Authenticated users only; binds order to the JWT subject
    @PostMapping("/create-order")
    public String createOrder(@RequestParam int amount,
                              @RequestParam(defaultValue = "INR") String currency,
                              @AuthenticationPrincipal Jwt jwt) throws Exception {
        String keycloakUserId = jwt.getClaimAsString("sub");
        return razorpayService.createOrder(amount, currency, keycloakUserId);
    }
}
