
package com.eventhub.backend.repository;

import com.eventhub.backend.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByOwnerId(Long ownerId);

}
