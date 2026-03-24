package com.publicis.sapient.Booking_Service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.publicis.sapient.Booking_Service.entities.Booking;
import com.publicis.sapient.Booking_Service.services.BookingCommandService;

@RestController
@RequestMapping("/api/v1/command")
public class BookingCommandController {

    @Autowired
    private BookingCommandService bookingCommandService;

    // Lock multiple seats — generates a booking with INPROGRESS status
    @PostMapping("/seats/lock")
    public Booking lockSeats(@RequestBody List<Long> seatIds) {
        return bookingCommandService.lockSeats(seatIds);
    }

    // Confirm a booking — marks it COMPLETE, seats become BOOKED
    @PutMapping("/bookings/{bookingId}/confirm")
    public Booking confirmBooking(@PathVariable String bookingId) {
        return bookingCommandService.confirmBooking(bookingId);
    }

    // Cancel a booking — marks it INCOMPLETE, seats become AVAILABLE
    @PutMapping("/bookings/{bookingId}/cancel")
    public Booking cancelBooking(@PathVariable String bookingId) {
        return bookingCommandService.cancelBooking(bookingId);
    }
}
