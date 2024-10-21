package org.event.service.location;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationEntityConverter entityConverter;

    public Location createLocation(Location locationDto) {
        var location = entityConverter.toEntity(locationDto);
        var locationEntity = locationRepository.save(location);

        return entityConverter.toDomain(locationEntity);
    }

    public List<Location> getLocations() {
        return locationRepository.findAll().stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public Location findLocationById(Long locationId) {
        var locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location with id= " + locationId + " not found"));
        return entityConverter.toDomain(locationEntity);
    }

    public void deleteLocation(Long locationId) {
        var locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location with id= " + locationId + " not found"));
        locationRepository.delete(locationEntity);
    }


    public Location updateLocation(Long locationId, Location locationDto) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Location with id= " + locationId + " not found");
        }

        locationRepository.updateLocation(
                locationId,
                locationDto.name(),
                locationDto.address(),
                locationDto.capacity(),
                locationDto.description()
        );
        return entityConverter.toDomain(
                locationRepository.findById(locationId)
                .orElseThrow()
        );
    }
}
