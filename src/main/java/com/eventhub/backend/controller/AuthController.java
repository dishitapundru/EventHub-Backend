package com.eventhub.backend.controller;

import com.eventhub.backend.dto.LoginResponse;
import com.eventhub.backend.dto.LoginRequest;
import com.eventhub.backend.dto.RegisterRequest;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.entity.Club;
import com.eventhub.backend.repository.UserRepository;
import com.eventhub.backend.repository.ClubRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            ClubRepository clubRepository
    ) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(request.getRole());

        // New club accounts must wait for admin approval.
        if ("CLUB".equalsIgnoreCase(request.getRole())) {
            user.setStatus("PENDING");
        } else {
            user.setStatus("APPROVED");
        }

        User savedUser = userRepository.save(user);

        /*
         * If the user registers as a CLUB,
         * automatically create a Club profile
         * and connect it to this user.
         */
        if ("CLUB".equalsIgnoreCase(request.getRole())) {

            Club club = new Club();

            club.setName(request.getName());
            club.setCategory("General");
            club.setMembers(0);
            club.setStatus("PENDING");
            club.setOwner(savedUser);

            clubRepository.save(club);
        }

        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

       return ResponseEntity.ok(
        new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        )
);
    }
}