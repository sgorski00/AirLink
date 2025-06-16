package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.repository.FlightRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirplaneService airplaneService;

    @CachePut(value = "flights", key = "#flight.id")
    public Flight save(Flight flight) {
        Airplane airplane = airplaneService.findByIdWithFlights(flight.getAirplane().getId());
        if (!airplane.isAvailable(flight.getDeparture(), flight.getArrival())) {
            throw new IllegalArgumentException("Airplane is not available for the specified time.");
        }
        return flightRepository.save(flight);
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Page<Flight> findAllActivePaginated(Pageable pageable, Long airportFrom, Long airportTo) {
        return flightRepository.findAllActiveFiltered(pageable, airportFrom, airportTo);
    }

    @Cacheable(value = "flights", key = "#id")
    public Flight findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        Flight flight = flightRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Flight not found")
        );
        if (flight.isActive() || isAdmin) {
            return flight;
        } else {
            throw new NoSuchElementException("Flight is no longer available active");
        }
    }

    public long count() {
        return flightRepository.count();
    }

    @CachePut(value = "flights", key = "#id")
    public Flight deleteFlightById(Long id) {
        Flight flight = flightRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Flight not found")
        );
        if (!flight.isActive()) {
            throw new IllegalArgumentException("Cannot delete a flight that has already departed.");
        }
        flight.setDeletedAt(Timestamp.from(Instant.now()));
        return flightRepository.save(flight);
    }

    @CachePut(value = "flights", key = "#id")
    public Flight restoreById(Long id) {
        Flight flight = flightRepository.findDeletedById(id).orElseThrow(
                () -> new NoSuchElementException("Flight not found or not deleted")
        );
        flight.setDeletedAt(null);
        return flightRepository.save(flight);
    }

    public Flight findByIdWithReservations(Long flightId) {
        return flightRepository.findByIdWithReservations(flightId).orElseThrow(
                () -> new NoSuchElementException("Flight with id: " + flightId + " not found")
        );
    }
}
