
package com.eventhub.backend.repository;

import com.eventhub.backend.entity.ClubUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubUpdateRepository
        extends JpaRepository<ClubUpdate, Long> {

    List<ClubUpdate> findByClubIdOrderByCreatedAtDesc(Long clubId);
}
