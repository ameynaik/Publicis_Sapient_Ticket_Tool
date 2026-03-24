package com.publicis.sapient.Ticket_Tool.reddis.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("City")
public record City(@Id int id, String name){}
    

