package com.publicis.sapient.Ticket_Tool.reddis.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.publicis.sapient.Ticket_Tool.reddis.entities.City;

@Repository
public interface CityRepository extends CrudRepository<City, Integer> {
}
