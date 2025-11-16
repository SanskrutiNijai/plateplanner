package com.plateplanner.paymentservice.repository;

import com.plateplanner.paymentservice.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    boolean existsByEventId(String eventId);
}
