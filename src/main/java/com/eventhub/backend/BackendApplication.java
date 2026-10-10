package com.eventhub.backend;

import com.eventhub.backend.entity.Club;
import com.eventhub.backend.entity.Event;
import com.eventhub.backend.entity.User;

import com.eventhub.backend.repository.ClubRepository;
import com.eventhub.backend.repository.EventRepository;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class BackendApplication {


public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
}

@Bean
CommandLineRunner createInitialData(
        EventRepository eventRepository,
        UserRepository userRepository,
        ClubRepository clubRepository
) {
    return args -> {

        // =========================
        // CREATE EVENTS
        // =========================

        if (eventRepository.count() == 0) {

            eventRepository.save(
                new Event(
                    "Tech Innovators Summit",
                    LocalDate.of(2026, 10, 18),
                    "Main Auditorium",
                    120
                )
            );

            eventRepository.save(
                new Event(
                    "CodeSprint Hackathon",
                    LocalDate.of(2026, 10, 25),
                    "Innovation Lab",
                    80
                )
            );

            eventRepository.save(
                new Event(
                    "AI & Machine Learning Workshop",
                    LocalDate.of(2026, 11, 2),
                    "Seminar Hall",
                    45
                )
            );

            System.out.println(
                "3 EventHub events created successfully!"
            );
        }

        // =========================
        // CREATE CLUBS
        // =========================

        if (clubRepository.count() == 0) {

            clubRepository.save(
                new Club(
                    "Coding Club",
                    "Technology",
                    42,
                    "Active"
                )
            );

            clubRepository.save(
                new Club(
                    "AI & Robotics Club",
                    "Technology",
                    35,
                    "Active"
                )
            );

            clubRepository.save(
                new Club(
                    "Literary Club",
                    "Arts",
                    28,
                    "Active"
                )
            );

            clubRepository.save(
                new Club(
                    "Photography Club",
                    "Arts",
                    21,
                    "Active"
                )
            );

            clubRepository.save(
                new Club(
                    "Sports Club",
                    "Sports",
                    50,
                    "Active"
                )
            );

            System.out.println(
                "5 EventHub clubs created successfully!"
            );
        }

        // =========================
        // CREATE / UPDATE ADMIN
        // =========================

        BCryptPasswordEncoder passwordEncoder =
                new BCryptPasswordEncoder();

        User admin;

        if (userRepository
                .findByEmail("admin@eventhub.com")
                .isEmpty()) {

            admin = new User();

            admin.setName("EventHub Admin");
            admin.setEmail("admin@eventhub.com");
            admin.setRole("ADMIN");

            admin.setPassword(
                passwordEncoder.encode("admin123")
            );

            userRepository.save(admin);

            System.out.println(
                "EventHub Admin account created successfully!"
            );

        } else {

            admin = userRepository
                    .findByEmail("admin@eventhub.com")
                    .get();

            admin.setPassword(
                passwordEncoder.encode("admin123")
            );

            admin.setRole("ADMIN");

            userRepository.save(admin);

            System.out.println(
                "EventHub Admin account updated successfully!"
            );
        }
    };
}

}
