package com.publicis.sapient.Ticket_Tool.reddis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.publicis.sapient.Ticket_Tool.reddis.entities.Theatre;

@Repository
public interface TheatreRepository extends CrudRepository<Theatre, Integer> {

    List<Theatre> findByCityId(Integer cityId);
}
