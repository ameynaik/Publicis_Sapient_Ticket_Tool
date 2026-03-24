package com.publicis.sapient.Booking_Service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.publicis.sapient.Booking_Service.entities.Booking;
import com.publicis.sapient.Booking_Service.entities.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByStatus(BookingStatus status);
}
