package com.publicis.sapient.Ticket_Tool.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.publicis.sapient.Ticket_Tool.data.entities.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByTheatreEventId(Integer theatreEventId);
}
