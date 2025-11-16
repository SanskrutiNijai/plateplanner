// WebhookUpgradeService.java (paymentservice)
package com.plateplanner.paymentservice.service;

import com.plateplanner.paymentservice.keycloak.KeycloakRoleService;
import com.plateplanner.paymentservice.model.Payment;
import com.plateplanner.paymentservice.repository.PaymentRepository;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookUpgradeService {

    private final PaymentRepository paymentRepo;
    private final KeycloakRoleService keycloakRoleService;
    private final MongoTemplate mongo; // <-- inject MongoTemplate

    @Value("${razorpay.webhook.secret}") private String webhookSecret;

    public void processWebhook(String rawBody, String signature) throws Exception {
        // 1) verify
        if (signature == null || signature.isBlank())
            throw new IllegalArgumentException("Missing X-Razorpay-Signature");
        Utils.verifyWebhookSignature(rawBody, signature, webhookSecret);

        // 2) parse
        JSONObject event = new JSONObject(rawBody);
        String eventType = event.optString("event", null);
        JSONObject payload = event.optJSONObject("payload"); if (payload == null) payload = new JSONObject();

        JSONObject orderEntity   = payload.has("order")   ? payload.getJSONObject("order").optJSONObject("entity")   : null;
        JSONObject paymentEntity = payload.has("payment") ? payload.getJSONObject("payment").optJSONObject("entity") : null;
        JSONObject entity = (paymentEntity != null) ? paymentEntity : orderEntity;

        String paymentId = (paymentEntity != null) ? paymentEntity.optString("id", null) : null;
        String orderId   = (orderEntity   != null) ? orderEntity.optString("id", null)   : null;
        Long amount      = (entity != null && entity.has("amount")) ? entity.optLong("amount") : null;
        String currency  = (entity != null) ? entity.optString("currency", null) : null;
        String status    = (entity != null) ? entity.optString("status", null)   : null;

        String keycloakId = (entity != null && entity.has("notes"))
                ? entity.getJSONObject("notes").optString("user_keycloak_id", null)
                : null;

        // 3) idempotent save of event
        String dedupeKey = (paymentId != null && eventType != null)
                ? "payment:" + paymentId + ":" + eventType
                : "hash:" + Integer.toHexString(rawBody.hashCode());

        if (!paymentRepo.existsByEventId(dedupeKey)) {
            paymentRepo.save(Payment.builder()
                    .eventId(dedupeKey)
                    .eventType(eventType)
                    .orderId(orderId)
                    .paymentId(paymentId)
                    .status(status)
                    .amount(amount)
                    .currency(currency)
                    .keycloakId(keycloakId)
                    .raw(event.toMap())
                    .build());
        }

        // 4) success? -> flip premium in Mongo directly, then assign Keycloak role
        boolean success = "payment.captured".equals(eventType) || "order.paid".equals(eventType);
        if (success && keycloakId != null) {
            // Mongo: users collection, where keycloakId matches
            Query q = new Query(Criteria.where("keycloakId").is(keycloakId));
            Update u = new Update().set("premium", true);
            mongo.updateFirst(q, u, "users"); // <-- collection name only

            // Keycloak: assign realm role (optional but recommended)
            try {
                keycloakRoleService.assignPremiumRole(keycloakId);
            } catch (Exception ex) {
                // log and continue (DB flag is already true)
                ex.printStackTrace();
            }
        }
    }
}
