package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Attendance;
import com.eventhub.backend.entity.EventRegistration;
import com.eventhub.backend.repository.AttendanceRepository;
import com.eventhub.backend.repository.EventRegistrationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final EventRegistrationRepository registrationRepository;

    public AttendanceController(
            AttendanceRepository attendanceRepository,
            EventRegistrationRepository registrationRepository) {

        this.attendanceRepository = attendanceRepository;
        this.registrationRepository = registrationRepository;
    }

    // ==========================================
    // MARK ATTENDANCE
    // ==========================================

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(
            @RequestParam Long registrationId) {

        EventRegistration registration =
                registrationRepository.findById(registrationId)
                        .orElse(null);

        if (registration == null) {
            return ResponseEntity.badRequest()
                    .body("Registration not found");
        }

        Optional<Attendance> existingAttendance =
                attendanceRepository
                        .findByRegistrationId(registrationId);

        if (existingAttendance.isPresent()) {

            Attendance attendance =
                    existingAttendance.get();

            attendance.setAttended(true);

            attendanceRepository.save(attendance);

            return ResponseEntity.ok(
                    "Attendance updated successfully"
            );
        }

        Attendance attendance =
                new Attendance(
                        registration,
                        true
                );

        attendanceRepository.save(attendance);

        return ResponseEntity.ok(
                "Attendance marked successfully"
        );
    }

    // ==========================================
    // GET ATTENDANCE FOR ONE REGISTRATION
    // ==========================================

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<?> getAttendance(
            @PathVariable Long registrationId) {

        Optional<Attendance> attendance =
                attendanceRepository
                        .findByRegistrationId(registrationId);

        if (attendance.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(
                attendance.get()
        );
    }

    // ==========================================
    // GET ATTENDANCE FOR ENTIRE CLUB
    // ==========================================

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<Attendance>> getClubAttendance(
            @PathVariable Long clubId) {

        List<Attendance> attendance =
                attendanceRepository
                        .findByRegistrationEventClubId(clubId);

        return ResponseEntity.ok(attendance);
    }
}