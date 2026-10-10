
package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Club;
import com.eventhub.backend.entity.ClubMembership;
import com.eventhub.backend.entity.User;

import com.eventhub.backend.repository.ClubRepository;
import com.eventhub.backend.repository.ClubMembershipRepository;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ClubMembershipRepository clubMembershipRepository;

    public ClubController(
            ClubRepository clubRepository,
            UserRepository userRepository,
            ClubMembershipRepository clubMembershipRepository
    ) {
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.clubMembershipRepository = clubMembershipRepository;
    }

    // GET ALL CLUBS
    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        return ResponseEntity.ok(clubRepository.findAll());
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
                .filter(club ->
                        "PENDING".equalsIgnoreCase(club.getStatus()))
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

                    return ResponseEntity.ok(
                            clubRepository.save(club)
                    );
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

                    return ResponseEntity.ok(
                            clubRepository.save(club)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE CLUB
    @PostMapping
    public ResponseEntity<Club> createClub(
            @RequestBody Club club
    ) {
        if (club.getMembers() == null) {
            club.setMembers(0);
        }

        Club savedClub = clubRepository.save(club);
        return ResponseEntity.ok(savedClub);
    }

    // UPDATE CLUB BY ID
    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(
            @PathVariable Long id,
            @RequestBody Club clubDetails
    ) {
        return clubRepository.findById(id)
                .map(club -> {
                    if (clubDetails.getName() != null) {
                        club.setName(clubDetails.getName());
                    }

                    if (clubDetails.getCategory() != null) {
                        club.setCategory(clubDetails.getCategory());
                    }

                    if (clubDetails.getStatus() != null) {
                        club.setStatus(clubDetails.getStatus());
                    }

                    if (clubDetails.getAbout() != null) {
                        club.setAbout(clubDetails.getAbout());
                    }

                    if (clubDetails.getHistory() != null) {
                        club.setHistory(clubDetails.getHistory());
                    }

                    if (clubDetails.getPastEvents() != null) {
                        club.setPastEvents(clubDetails.getPastEvents());
                    }

                    if (clubDetails.getAchievements() != null) {
                        club.setAchievements(
                                clubDetails.getAchievements()
                        );
                    }

                    return ResponseEntity.ok(
                            clubRepository.save(club)
                    );
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
                    if (clubDetails.getName() != null) {
                        club.setName(clubDetails.getName());
                    }

                    if (clubDetails.getCategory() != null) {
                        club.setCategory(clubDetails.getCategory());
                    }

                    if (clubDetails.getAbout() != null) {
                        club.setAbout(clubDetails.getAbout());
                    }

                    if (clubDetails.getHistory() != null) {
                        club.setHistory(clubDetails.getHistory());
                    }

                    if (clubDetails.getPastEvents() != null) {
                        club.setPastEvents(clubDetails.getPastEvents());
                    }

                    if (clubDetails.getAchievements() != null) {
                        club.setAchievements(
                                clubDetails.getAchievements()
                        );
                    }

                    // Do not let profile edits overwrite member count.
                    return ResponseEntity.ok(
                            clubRepository.save(club)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // JOIN CLUB
    @PostMapping("/{clubId}/join/{studentId}")
    @Transactional
    public ResponseEntity<?> joinClub(
            @PathVariable Long clubId,
            @PathVariable Long studentId
    ) {
        var clubOptional = clubRepository.findById(clubId);
        var studentOptional = userRepository.findById(studentId);

        if (clubOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (studentOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Student not found.");
        }

        Club club = clubOptional.get();
        User student = studentOptional.get();

        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            return ResponseEntity.badRequest()
                    .body("Only students can join a club.");
        }

        if (clubMembershipRepository
                .existsByClub_IdAndStudent_Id(clubId, studentId)) {
            return ResponseEntity.ok(club);
        }

        ClubMembership membership =
                new ClubMembership(club, student);

        clubMembershipRepository.saveAndFlush(membership);

        int currentMembers = club.getMembers() == null
                ? 0
                : club.getMembers();

        club.setMembers(currentMembers + 1);

        Club updatedClub = clubRepository.save(club);

        return ResponseEntity.ok(updatedClub);
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
