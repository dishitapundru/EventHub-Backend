package com.eventhub.backend.controller;

import com.eventhub.backend.entity.EventFeedback;
import com.eventhub.backend.entity.EventRegistration;
import com.eventhub.backend.repository.EventFeedbackRepository;
import com.eventhub.backend.repository.EventRegistrationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
public class EventFeedbackController {

    private final EventFeedbackRepository feedbackRepository;
    private final EventRegistrationRepository registrationRepository;

    public EventFeedbackController(
            EventFeedbackRepository feedbackRepository,
            EventRegistrationRepository registrationRepository) {

        this.feedbackRepository = feedbackRepository;
        this.registrationRepository = registrationRepository;
    }

    // ==========================================
    // SUBMIT FEEDBACK
    // ==========================================

    @PostMapping
    public ResponseEntity<?> submitFeedback(
            @RequestParam Long registrationId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment) {

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest()
                    .body("Rating must be between 1 and 5");
        }

        EventRegistration registration =
                registrationRepository.findById(registrationId)
                        .orElse(null);

        if (registration == null) {
            return ResponseEntity.badRequest()
                    .body("Registration not found");
        }

        Optional<EventFeedback> existingFeedback =
                feedbackRepository.findByRegistrationId(
                        registrationId
                );

        if (existingFeedback.isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Feedback already submitted for this event");
        }

        EventFeedback feedback =
                new EventFeedback(
                        registration,
                        rating,
                        comment
                );

        feedbackRepository.save(feedback);

        return ResponseEntity.ok(
                "Feedback submitted successfully"
        );
    }

    // ==========================================
    // GET FEEDBACK FOR ONE REGISTRATION
    // ==========================================

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<?> getRegistrationFeedback(
            @PathVariable Long registrationId) {

        Optional<EventFeedback> feedback =
                feedbackRepository.findByRegistrationId(
                        registrationId
                );

        if (feedback.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(feedback.get());
    }

    // ==========================================
    // GET FEEDBACK FOR ENTIRE CLUB
    // ==========================================

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<EventFeedback>> getClubFeedback(
            @PathVariable Long clubId) {

        List<EventFeedback> feedback =
                feedbackRepository
                        .findByRegistrationEventClubId(clubId);

        return ResponseEntity.ok(feedback);
    }
}