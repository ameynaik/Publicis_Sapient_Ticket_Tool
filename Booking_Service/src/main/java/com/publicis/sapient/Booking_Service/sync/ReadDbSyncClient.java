package com.publicis.sapient.Booking_Service.sync;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publicis.sapient.Booking_Service.entities.Seat;
import com.publicis.sapient.Booking_Service.kafka.SeatSyncProducer;

/**
 * Publishes seat state changes to Kafka.
 * All Query (read) microservices with their own consumer groups
 * will receive and apply these updates to their own read databases.
 */
@Component
public class ReadDbSyncClient {

    @Autowired
    private SeatSyncProducer seatSyncProducer;

    public void syncSeatsToReadDb(List<Seat> seats) {
        seatSyncProducer.publishSeatSyncEvent(seats);
    }
}
