package com.publicis.sapient.Ticket_Tool.reddis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.publicis.sapient.Ticket_Tool.EventType;
import com.publicis.sapient.Ticket_Tool.reddis.entities.Events;

@Repository
public interface EventsRepository extends CrudRepository<Events, String> {

    List<Events> findByTypeAndCityId(EventType type, Integer cityId);

    List<Events> findByCityId(Integer cityId);
}
