package com.publicis.sapient.Booking_Service.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.publicis.sapient.Booking_Service.entities.Booking;
import com.publicis.sapient.Booking_Service.entities.BookingStatus;
import com.publicis.sapient.Booking_Service.entities.OccupancyStatus;
import com.publicis.sapient.Booking_Service.entities.Seat;
import com.publicis.sapient.Booking_Service.repositories.BookingRepository;
import com.publicis.sapient.Booking_Service.repositories.SeatRepository;
import com.publicis.sapient.Booking_Service.sync.ReadDbSyncClient;

@Service
public class BookingCommandService {

    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReadDbSyncClient readDbSyncClient;

    /**
     * Locks multiple seats in one go.
     * Generates a bookingId, creates a Booking with status INPROGRESS,
     * and syncs the changes to the read database.
     */
    @Transactional
    public Booking lockSeats(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("One or more seats not found");
        }

        // Release any expired locks on these seats first
        releaseExpiredLocks(seats);

        // Verify all requested seats are available
        for (Seat seat : seats) {
            if (seat.getOccupancyStatus() != OccupancyStatus.AVAILABLE) {
                throw new RuntimeException(
                        "Seat " + seat.getSeatNumber() + " is not available. Status: " + seat.getOccupancyStatus());
            }
        }

        // Create booking with INPROGRESS status
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, BookingStatus.INPROGRESS, LocalDateTime.now());
        bookingRepository.save(booking);

        // Lock all seats
        LocalDateTime now = LocalDateTime.now();
        for (Seat seat : seats) {
            seat.setOccupancyStatus(OccupancyStatus.LOCKED);
            seat.setLockedAt(now);
            seat.setBooking(booking);
        }
        seatRepository.saveAll(seats);
        booking.setSeats(seats);

        // Sync to read DB
        readDbSyncClient.syncSeatsToReadDb(seats);

        return booking;
    }

    /**
     * Confirms a booking — marks it COMPLETE and seats as BOOKED.
     */
    @Transactional
    public Booking confirmBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.INPROGRESS) {
            throw new RuntimeException("Booking is not in INPROGRESS state. Current: " + booking.getStatus());
        }

        // Check if lock has expired
        List<Seat> seats = booking.getSeats();
        boolean anyExpired = seats.stream()
                .anyMatch(this::isLockExpired);

        if (anyExpired) {
            // Mark as INCOMPLETE — lock expired before payment
            booking.setStatus(BookingStatus.INCOMPLETE);
            bookingRepository.save(booking);
            for (Seat seat : seats) {
                resetSeat(seat);
            }
            seatRepository.saveAll(seats);
            readDbSyncClient.syncSeatsToReadDb(seats);
            throw new RuntimeException("Lock expired. Booking marked as INCOMPLETE.");
        }

        // Confirm booking
        booking.setStatus(BookingStatus.COMPLETE);
        bookingRepository.save(booking);

        for (Seat seat : seats) {
            seat.setOccupancyStatus(OccupancyStatus.BOOKED);
            seat.setLockedAt(null);
        }
        seatRepository.saveAll(seats);

        // Sync to read DB
        readDbSyncClient.syncSeatsToReadDb(seats);

        return booking;
    }

    /**
     * Cancels a booking — marks it INCOMPLETE and releases seats.
     */
    @Transactional
    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        booking.setStatus(BookingStatus.INCOMPLETE);
        bookingRepository.save(booking);

        List<Seat> seats = booking.getSeats();
        for (Seat seat : seats) {
            resetSeat(seat);
        }
        seatRepository.saveAll(seats);

        // Sync to read DB
        readDbSyncClient.syncSeatsToReadDb(seats);

        return booking;
    }

    /**
     * Releases locks for specific seat IDs (received from Kafka)
     * and marks their bookings as INCOMPLETE.
     */
    @Transactional
    public void releaseLocksForSeatIds(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        for (Seat seat : seats) {
            if (seat.getOccupancyStatus() == OccupancyStatus.LOCKED) {
                Booking booking = seat.getBooking();
                if (booking != null && booking.getStatus() == BookingStatus.INPROGRESS) {
                    booking.setStatus(BookingStatus.INCOMPLETE);
                    bookingRepository.save(booking);
                }
                resetSeat(seat);
                seatRepository.save(seat);
            }
        }
    }

    /**
     * Releases expired locks and marks their bookings as INCOMPLETE.
     */
    private void releaseExpiredLocks(List<Seat> seats) {
        for (Seat seat : seats) {
            if (seat.getOccupancyStatus() == OccupancyStatus.LOCKED && isLockExpired(seat)) {
                Booking booking = seat.getBooking();
                if (booking != null && booking.getStatus() == BookingStatus.INPROGRESS) {
                    booking.setStatus(BookingStatus.INCOMPLETE);
                    bookingRepository.save(booking);
                }
                resetSeat(seat);
                seatRepository.save(seat);
                readDbSyncClient.syncSeatsToReadDb(List.of(seat));
            }
        }
    }

    private boolean isLockExpired(Seat seat) {
        return seat.getLockedAt() != null
                && Duration.between(seat.getLockedAt(), LocalDateTime.now()).compareTo(LOCK_DURATION) > 0;
    }

    private void resetSeat(Seat seat) {
        seat.setOccupancyStatus(OccupancyStatus.AVAILABLE);
        seat.setLockedAt(null);
        seat.setBooking(null);
    }
}
