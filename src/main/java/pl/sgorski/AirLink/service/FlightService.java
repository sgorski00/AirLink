package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.model.Flight;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightCacheService flightCacheService;
    private final AirplaneService airplaneService;

    public Flight save(Flight flight) {
        Airplane airplane = airplaneService.findByIdWithFlights(flight.getAirplane().getId());
        if (!airplane.isAvailable(flight)) {
            throw new IllegalArgumentException("Airplane is not available for the specified time.");
        }
        return flightCacheService.save(flight);
    }

    public List<Flight> findAll() {
        return flightCacheService.findAll();
    }

    public Page<Flight> findAllActivePaginated(Pageable pageable, Long airportFrom, Long airportTo) {
        return flightCacheService.findAllActiveFiltered(pageable, airportFrom, airportTo);
    }

    public Flight findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        Flight flight = flightCacheService.findById(id);
        if (flight != null && (flight.isActive() || isAdmin)) {
            return flight;
        } else {
            throw new NoSuchElementException("Flight is no longer available active");
        }
    }

    public long count() {
        return flightCacheService.count();
    }

    public Flight deleteById(Long id) {
        Flight flight = flightCacheService.findById(id);
        if (!flight.isActive()) {
            throw new IllegalArgumentException("Cannot delete a flight that has already departed.");
        }
        flight.delete();
        return flightCacheService.save(flight);
    }

    public Flight restoreById(Long id) {
        Flight flight = flightCacheService.findDeletedById(id);
        flight.restore();
        return flightCacheService.save(flight);
    }

    public Flight findByIdWithReservations(Long flightId) {
        return flightCacheService.findByIdWithReservations(flightId);
    }
}
