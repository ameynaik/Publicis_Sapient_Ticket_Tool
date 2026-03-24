package com.publicis.sapient.Ticket_Tool.kafka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Publishes lock-release requests to Kafka with the seat IDs to release.
 * The Command (Booking) service consumes these to release locks in the write DB.
 */
@Component
public class LockReleaseProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishLockReleaseEvent(List<Long> seatIds) {
        try {
            String message = objectMapper.writeValueAsString(seatIds);
            kafkaTemplate.send(KafkaTopics.LOCK_RELEASE_EVENTS, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
