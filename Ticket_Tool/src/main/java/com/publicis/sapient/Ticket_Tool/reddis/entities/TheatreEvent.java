package com.publicis.sapient.Ticket_Tool.reddis.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
@RedisHash("TheatreEvent")
public record TheatreEvent(@Id Integer theatreId, String eventName, LocalTime eventTime, @Indexed LocalDate eventDate) {
    
}
