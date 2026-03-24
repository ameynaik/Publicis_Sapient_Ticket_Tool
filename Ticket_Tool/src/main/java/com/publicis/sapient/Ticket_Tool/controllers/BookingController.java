package com.publicis.sapient.Ticket_Tool.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.publicis.sapient.Ticket_Tool.data.entities.Seat;
import com.publicis.sapient.Ticket_Tool.services.SeatQueryService;

@RestController
@RequestMapping("/api/v1/query")
public class BookingController {

    @Autowired
    private SeatQueryService seatQueryService;

    // Query: Get all seats for a TheatreEvent (read-only, releases expired locks)
    @GetMapping("/theatre-events/{theatreEventId}/seats")
    public List<Seat> getSeatsByTheatreEvent(@PathVariable Integer theatreEventId) {
        return seatQueryService.getSeatsByTheatreEventId(theatreEventId);
    }
}
