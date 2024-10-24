package org.event.service.event.registration;

import lombok.RequiredArgsConstructor;
import org.event.service.event.EventRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;


}
