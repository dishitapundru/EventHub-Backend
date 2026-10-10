
package com.eventhub.backend.repository;

import com.eventhub.backend.entity.ClubMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMembershipRepository
        extends JpaRepository<ClubMembership, Long> {

    boolean existsByClub_IdAndStudent_Id(
        Long clubId,
        Long studentId
    );
}
