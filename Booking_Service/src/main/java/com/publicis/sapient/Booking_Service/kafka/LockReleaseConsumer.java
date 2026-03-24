package com.publicis.sapient.Booking_Service.kafka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicis.sapient.Booking_Service.services.BookingCommandService;

/**
 * Consumes lock-release events published by Query (read) services.
 * Receives specific seat IDs whose locks have expired.
 * Even if the Command service was temporarily down, messages
 * stay in Kafka and are processed when the service recovers.
 */
@Component
public class LockReleaseConsumer {

    @Autowired
    private BookingCommandService bookingCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.LOCK_RELEASE_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeLockReleaseEvent(String message) {
        try {
            List<Long> seatIds = objectMapper.readValue(message, new TypeReference<List<Long>>() {});
            bookingCommandService.releaseLocksForSeatIds(seatIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
