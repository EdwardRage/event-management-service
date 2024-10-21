package org.event.service.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    @Modifying
    @Query("""
            update LocationEntity l 
            set l.name = :name, 
                l.address = :address,
                l.capacity = :capacity, 
                l.description = :description
            where l.id = :id
    """)
    void updateLocation(
            Long id,
            String name,
            String address,
            Integer capacity,
            String description
    );
}
