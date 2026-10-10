package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Club;
import com.eventhub.backend.entity.Event;
import com.eventhub.backend.repository.ClubRepository;
import com.eventhub.backend.repository.EventRepository;
import com.eventhub.backend.repository.EventRegistrationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final ClubRepository clubRepository;

    public EventController(
            EventRepository eventRepository,
            EventRegistrationRepository eventRegistrationRepository,
            ClubRepository clubRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.clubRepository = clubRepository;
    }

    // GET all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // GET one event
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {

        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET events belonging to a specific club
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<Event>> getEventsByClub(
            @PathVariable Long clubId
    ) {

        return ResponseEntity.ok(
                eventRepository.findByClubId(clubId)
        );
    }

    // CREATE event - existing Admin/Student functionality
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    // CREATE event for a specific club
    @PostMapping("/club")
    public ResponseEntity<?> createClubEvent(
            @RequestBody Map<String, Object> request
    ) {

        try {

            Long clubId = Long.valueOf(
                    request.get("clubId").toString()
            );

            Club club = clubRepository.findById(clubId)
                    .orElse(null);

            if (club == null) {
                return ResponseEntity.badRequest()
                        .body("Club not found");
            }

            Event event = new Event();

            event.setTitle(
                    request.get("title").toString()
            );

            event.setEventDate(
                    LocalDate.parse(
                            request.get("eventDate").toString()
                    )
            );

            event.setLocation(
                    request.get("location").toString()
            );

            event.setCapacity(
                    Integer.parseInt(
                            request.get("capacity").toString()
                    )
            );

            // Connect event to the club
            event.setClub(club);

            Event savedEvent = eventRepository.save(event);

            return ResponseEntity.ok(savedEvent);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("Unable to create event: " + e.getMessage());
        }
    }

    // UPDATE event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody Event eventDetails
    ) {

        return eventRepository.findById(id)
                .map(event -> {

                    event.setTitle(eventDetails.getTitle());
                    event.setEventDate(eventDetails.getEventDate());
                    event.setLocation(eventDetails.getLocation());
                    event.setCapacity(eventDetails.getCapacity());

                    Event updatedEvent =
                            eventRepository.save(event);

                    return ResponseEntity.ok(updatedEvent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE event
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id
    ) {

        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // First delete all registrations belonging to this event
        eventRegistrationRepository.deleteByEventId(id);

        // Then delete the event
        eventRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}