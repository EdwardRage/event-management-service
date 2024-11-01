package org.event.service.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {


    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
        select reg from RegistrationEntity reg
        where reg.event.id = :eventId and reg.user.id = :userId
        """)
    Optional<RegistrationEntity> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
        select reg.event.id from RegistrationEntity reg
        where reg.user.id = :userId
        """)
    List<Long> findEventIdsByUserId(Long userId);
}
