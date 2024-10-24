package org.event.service.event.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<RegistrationEntity> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
        select reg.eventId from RegistrationEntity reg
        where reg.userId = :userId
        """)
    List<Long> findEventIdsByUserId(Long userId);
}
