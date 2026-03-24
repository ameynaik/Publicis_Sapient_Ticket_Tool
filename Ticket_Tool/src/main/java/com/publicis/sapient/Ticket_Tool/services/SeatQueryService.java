package com.publicis.sapient.Ticket_Tool.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.publicis.sapient.Ticket_Tool.data.entities.OccupancyStatus;
import com.publicis.sapient.Ticket_Tool.data.entities.Seat;
import com.publicis.sapient.Ticket_Tool.data.repositories.SeatRepository;
import com.publicis.sapient.Ticket_Tool.kafka.LockReleaseProducer;

@Service
public class SeatQueryService {

    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private LockReleaseProducer lockReleaseProducer;

    /**
     * Returns all seats for a TheatreEvent.
     * 1. Releases expired locks in the READ DB immediately (fast user response).
     * 2. Publishes a Kafka event so the Command service syncs the release in the write DB.
     */
    @Transactional
    public List<Seat> getSeatsByTheatreEventId(Integer theatreEventId) {
        List<Seat> seats = seatRepository.findByTheatreEventId(theatreEventId);

        // Optimistic update: release expired locks in read DB right away
        List<Long> releasedSeatIds = releaseExpiredLocksInReadDb(seats);

        // Publish released seat IDs to Kafka — Command service will sync in write DB
        if (!releasedSeatIds.isEmpty()) {
            lockReleaseProducer.publishLockReleaseEvent(releasedSeatIds);
        }

        return seats;
    }

    /**
     * Releases expired locks directly in the read DB for instant user feedback.
     * Returns list of seat IDs that were released.
     */
    private List<Long> releaseExpiredLocksInReadDb(List<Seat> seats) {
        List<Long> releasedSeatIds = new ArrayList<>();
        for (Seat seat : seats) {
            if (seat.getOccupancyStatus() == OccupancyStatus.LOCKED && isLockExpired(seat)) {
                seat.setOccupancyStatus(OccupancyStatus.AVAILABLE);
                seat.setLockedAt(null);
                seat.setBooking(null);
                seatRepository.save(seat);
                releasedSeatIds.add(seat.getId());
            }
        }
        return releasedSeatIds;
    }

    private boolean isLockExpired(Seat seat) {
        return seat.getLockedAt() != null
                && Duration.between(seat.getLockedAt(), LocalDateTime.now()).compareTo(LOCK_DURATION) > 0;
    }
}
