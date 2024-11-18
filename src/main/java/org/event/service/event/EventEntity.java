package org.event.service.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.event.service.location.LocationEntity;
import org.event.service.registration.RegistrationEntity;
import org.event.service.user.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private Integer duration;
    private Integer cost;
    private Integer maxPlaces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Enumerated(EnumType.STRING)
    private EventStatus status;
    private Integer occupiedPlaces;

    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrationList;
}
