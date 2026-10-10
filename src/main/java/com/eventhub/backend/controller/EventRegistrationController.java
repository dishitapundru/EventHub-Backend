package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Event;
import com.eventhub.backend.entity.EventRegistration;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.repository.EventRegistrationRepository;
import com.eventhub.backend.repository.EventRepository;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class EventRegistrationController {

    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public EventRegistrationController(
            EventRegistrationRepository registrationRepository,
            UserRepository userRepository,
            EventRepository eventRepository) {

        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    // ==========================================
    // REGISTER FOR EVENT
    // ==========================================

    @PostMapping
    public ResponseEntity<?> registerForEvent(

            @RequestParam Long userId,
            @RequestParam Long eventId,

            @RequestParam String rollNumber,
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam String section,
            @RequestParam String phoneNumber
    ) {

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("User not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElse(null);

        if (event == null) {
            return ResponseEntity.badRequest()
                    .body("Event not found");
        }

        if (registrationRepository.existsByUserIdAndEventId(
                userId, eventId)) {

            return ResponseEntity.badRequest()
                    .body("You are already registered for this event");
        }

        EventRegistration registration =
                new EventRegistration(
                        user,
                        event,
                        rollNumber,
                        department,
                        year,
                        section,
                        phoneNumber
                );

        registrationRepository.save(registration);

        return ResponseEntity.ok(
                "Registration successful"
        );
    }

    // ==========================================
    // GET REGISTRATIONS FOR ONE USER
    // ==========================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventRegistration>> getUserRegistrations(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                registrationRepository.findByUserId(userId)
        );
    }

    // ==========================================
    // GET REGISTRATIONS FOR ONE CLUB
    // ==========================================

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<EventRegistration>> getClubRegistrations(
            @PathVariable Long clubId) {

        return ResponseEntity.ok(
                registrationRepository.findByEventClubId(clubId)
        );
    }
}