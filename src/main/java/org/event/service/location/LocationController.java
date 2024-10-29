package org.event.service.location;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;
    private final LocationDtoConverter dtoConverter;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LocationDto> createLocation(@RequestBody @Valid LocationDto locationDto) {
        Location locationResponse = locationService.createLocation(dtoConverter.toDomain(locationDto));
        log.info("New location added:{} ", locationResponse.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(locationResponse));
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(locationService.getLocations().stream()
                .map(dtoConverter::toDto)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable(name = "id") Long locationId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        dtoConverter.toDto(locationService.findLocationById(locationId))
                );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable(name = "id") Long locationId) {
        locationService.deleteLocation(locationId);
        log.info("Location deleted");
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public LocationDto updateLocation(
            @PathVariable(name = "id") Long locationId,
            @RequestBody @Valid LocationDto locationUpdateDto) {
        var locationUpdate = dtoConverter.toDomain(locationUpdateDto);
        LocationDto locationDto = dtoConverter.toDto(locationService.updateLocation(locationId, locationUpdate));
        log.info("Location with id={} update", locationId);
        return locationDto;
    }
}
