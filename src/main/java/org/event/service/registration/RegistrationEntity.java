package org.event.service.registration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.event.service.event.EventEntity;
import org.event.service.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDateTime created;
}
