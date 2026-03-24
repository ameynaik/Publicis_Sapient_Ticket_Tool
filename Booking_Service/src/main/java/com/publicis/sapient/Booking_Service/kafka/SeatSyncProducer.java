package com.publicis.sapient.Booking_Service.kafka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicis.sapient.Booking_Service.entities.Seat;

/**
 * Publishes seat state changes to Kafka.
 * All Query (read) microservices subscribe to this topic
 * to update their own read databases.
 */
@Component
public class SeatSyncProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishSeatSyncEvent(List<Seat> seats) {
        try {
            String message = objectMapper.writeValueAsString(seats);
            kafkaTemplate.send(KafkaTopics.SEAT_SYNC_EVENTS, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
