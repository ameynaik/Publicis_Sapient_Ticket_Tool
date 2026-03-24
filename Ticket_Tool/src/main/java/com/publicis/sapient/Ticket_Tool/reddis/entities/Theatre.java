package com.publicis.sapient.Ticket_Tool.reddis.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("Theatre")
public record Theatre(@Id Integer theatreId, String name,@Indexed Integer cityId, List<String> theatreEvent) {}
