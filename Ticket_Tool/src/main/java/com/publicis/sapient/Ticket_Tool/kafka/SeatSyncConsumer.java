package com.publicis.sapient.Ticket_Tool.kafka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicis.sapient.Ticket_Tool.data.entities.Seat;
import com.publicis.sapient.Ticket_Tool.data.repositories.SeatRepository;

/**
 * Consumes seat-sync events published by the Command (Booking) service.
 * Updates the read database with the latest seat state.
 * Multiple Query microservices can each have their own consumer group
 * to maintain their own read DB.
 */
@Component
public class SeatSyncConsumer {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.SEAT_SYNC_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSeatSyncEvent(String message) {
        try {
            List<Seat> seats = objectMapper.readValue(message, new TypeReference<List<Seat>>() {});
            seatRepository.saveAll(seats);
        } catch (Exception e) {
            // Log error — failed to sync seat data
            e.printStackTrace();
        }
    }
}
