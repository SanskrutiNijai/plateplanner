package com.plateplanner.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.api.key-id}")    private String apiKey;
    @Value("${razorpay.api.key-secret}") private String apiSecret;

    public String createOrder(int amount, String currency, String keycloakUserId) throws Exception {
        RazorpayClient client = new RazorpayClient(apiKey, apiSecret);

        JSONObject req = new JSONObject();
        req.put("amount", amount * 100); // paise
        req.put("currency", currency);
        req.put("receipt", "premium_upgrade");

        JSONObject notes = new JSONObject();
        notes.put("user_keycloak_id", keycloakUserId);
        req.put("notes", notes);

        Order order = client.orders.create(req);
        return order.toString();
    }
}
