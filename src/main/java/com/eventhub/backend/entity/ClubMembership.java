
package com.eventhub.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "club_memberships",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"club_id", "student_id"}
    )
)
public class ClubMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    public ClubMembership() {
    }

    public ClubMembership(Club club, User student) {
        this.club = club;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }
}
