
package com.eventhub.backend.repository;

import com.eventhub.backend.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRegistrationRepository
        extends JpaRepository<EventRegistration, Long> {

    boolean existsByUserIdAndEventId(
            Long userId,
            Long eventId
    );

    List<EventRegistration> findByUserId(
            Long userId
    );

    void deleteByEventId(
            Long eventId
    );

    List<EventRegistration> findByEventClubId(
            Long clubId
    );
}
