package com.plateplanner.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    private String eventId;    // e.g. payment:<paymentId>:<eventType>
    private String eventType;  // payment.captured / order.paid
    private String orderId;
    private String paymentId;

    private String keycloakId;
    private String status;

    private Long amount;
    private String currency;

    private Map<String, Object> raw; // full webhook payload (for audits)
}
