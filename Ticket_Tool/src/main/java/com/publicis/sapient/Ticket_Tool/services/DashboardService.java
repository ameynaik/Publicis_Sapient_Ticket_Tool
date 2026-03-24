package com.publicis.sapient.Ticket_Tool.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.publicis.sapient.Ticket_Tool.EventType;
import com.publicis.sapient.Ticket_Tool.reddis.entities.City;
import com.publicis.sapient.Ticket_Tool.reddis.entities.Event;
import com.publicis.sapient.Ticket_Tool.reddis.entities.Theatre;
import com.publicis.sapient.Ticket_Tool.reddis.entities.TheatreEvent;
import com.publicis.sapient.Ticket_Tool.reddis.repositories.CityRepository;
import com.publicis.sapient.Ticket_Tool.reddis.repositories.EventsRepository;
import com.publicis.sapient.Ticket_Tool.reddis.repositories.TheatreEventRepository;
import com.publicis.sapient.Ticket_Tool.reddis.repositories.TheatreRepository;

@Service
public class DashboardService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private TheatreEventRepository theatreEventRepository;

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        cityRepository.findAll().forEach(cities::add);
        return cities;
    }

    public List<Event> getEventsByCityId(Integer cityId) {
        return eventsRepository.findByCityId(cityId);
    }

    public List<Event> getEventsByTypeAndCity(EventType type, Integer cityId) {
        return eventsRepository.findByTypeAndCityId(type, cityId);
    }

    public List<Theatre> getTheatresByCityId(Integer cityId) {
        return theatreRepository.findByCityId(cityId);
    }

    public List<Theatre> getTheatresByEventAndCity(String eventName, Integer cityId) {
        return theatreRepository.findByCityId(cityId).stream()
                .filter(theatre -> theatre.theatreEvent() != null && theatre.theatreEvent().contains(eventName))
                .toList();
    }

    public List<TheatreEvent> getTheatreEventsByDateAndId(LocalDate date, Integer theatreId) {
        return theatreEventRepository.findByEventDateAndTheatreId(date, theatreId);
    }
}
