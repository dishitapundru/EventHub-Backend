
package com.eventhub.backend.repository;

import com.eventhub.backend.entity.EventFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventFeedbackRepository
        extends JpaRepository<EventFeedback, Long> {

    List<EventFeedback>
    findByRegistrationEventClubId(
            Long clubId
    );

    Optional<EventFeedback>
    findByRegistrationId(
            Long registrationId
    );
}
