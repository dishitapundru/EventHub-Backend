package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Club;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.repository.ClubRepository;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    public ClubController(
            ClubRepository clubRepository,
            UserRepository userRepository
    ) {
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
    }

    // GET ALL CLUBS
    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        return ResponseEntity.ok(
                clubRepository.findAll()
        );
    }

    // GET ONE CLUB
    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(
            @PathVariable Long id
    ) {
        return clubRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET CLUB BY OWNER USER ID
    @GetMapping("/owner/{userId}")
    public ResponseEntity<Club> getClubByOwner(
            @PathVariable Long userId
    ) {
        return clubRepository.findByOwnerId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET PENDING CLUB REQUESTS
   @GetMapping("/pending")
public ResponseEntity<List<Club>> getPendingClubs() {

    List<Club> pendingClubs = clubRepository.findAll()
            .stream()
            .filter(club -> "PENDING".equalsIgnoreCase(club.getStatus()))
            .toList();

    return ResponseEntity.ok(pendingClubs);
}
    // APPROVE CLUB
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveClub(
            @PathVariable Long id
    ) {

        return clubRepository.findById(id)
                .map(club -> {

                    club.setStatus("ACTIVE");

                    User owner = club.getOwner();

                    if (owner != null) {
                        owner.setStatus("APPROVED");
                        userRepository.save(owner);
                    }

                    Club approvedClub =
                            clubRepository.save(club);

                    return ResponseEntity.ok(approvedClub);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // REJECT CLUB
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectClub(
            @PathVariable Long id
    ) {

        return clubRepository.findById(id)
                .map(club -> {

                    club.setStatus("REJECTED");

                    User owner = club.getOwner();

                    if (owner != null) {
                        owner.setStatus("REJECTED");
                        userRepository.save(owner);
                    }

                    Club rejectedClub =
                            clubRepository.save(club);

                    return ResponseEntity.ok(rejectedClub);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE CLUB
    @PostMapping
    public ResponseEntity<Club> createClub(
            @RequestBody Club club
    ) {
        Club savedClub =
                clubRepository.save(club);

        return ResponseEntity.ok(savedClub);
    }

    // UPDATE CLUB
    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(
            @PathVariable Long id,
            @RequestBody Club clubDetails
    ) {
        return clubRepository.findById(id)
                .map(club -> {

                    club.setName(
                            clubDetails.getName()
                    );

                    club.setCategory(
                            clubDetails.getCategory()
                    );

                    club.setMembers(
                            clubDetails.getMembers()
                    );

                    club.setStatus(
                            clubDetails.getStatus()
                    );

                    Club updatedClub =
                            clubRepository.save(club);

                    return ResponseEntity.ok(updatedClub);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE CLUB PROFILE BY OWNER
    @PutMapping("/owner/{userId}")
    public ResponseEntity<Club> updateClubByOwner(
            @PathVariable Long userId,
            @RequestBody Club clubDetails
    ) {
        return clubRepository.findByOwnerId(userId)
                .map(club -> {

                    club.setName(
                            clubDetails.getName()
                    );

                    club.setCategory(
                            clubDetails.getCategory()
                    );

                    Club updatedClub =
                            clubRepository.save(club);

                    return ResponseEntity.ok(updatedClub);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE CLUB
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(
            @PathVariable Long id
    ) {
        if (!clubRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clubRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}