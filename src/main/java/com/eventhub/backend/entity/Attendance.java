
package com.eventhub.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "event_attendance",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"registration_id"})
    }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private EventRegistration registration;

    @Column(nullable = false)
    private boolean attended;

    @Column(nullable = false)
    private LocalDateTime markedAt;

    public Attendance() {
    }

    public Attendance(
            EventRegistration registration,
            boolean attended
    ) {
        this.registration = registration;
        this.attended = attended;
        this.markedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public EventRegistration getRegistration() {
        return registration;
    }

    public void setRegistration(EventRegistration registration) {
        this.registration = registration;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }
}
