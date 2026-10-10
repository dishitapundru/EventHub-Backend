
package com.eventhub.backend.repository;

import com.eventhub.backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByRegistrationId(
            Long registrationId
    );

    List<Attendance> findByRegistrationEventClubId(
            Long clubId
    );
}
