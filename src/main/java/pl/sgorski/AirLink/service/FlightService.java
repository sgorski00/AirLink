package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.repository.FlightRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirplaneService airplaneService;

    @CachePut(value = "flights", key = "#flight.id")
    public Flight save(Flight flight) {
        Airplane airplane = airplaneService.findByIdWithFlights(flight.getAirplane().getId());
        if(!airplane.isAvailable(flight.getDeparture(), flight.getArrival())) {
            throw new IllegalArgumentException("Airplane is not available for the specified time.");
        }
        return flightRepository.save(flight);
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    @Cacheable(value = "flights", key = "#id")
    public Flight findById(Long id) {
        return flightRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Flight not found")
        );
    }

    public long count() {
        return flightRepository.count();
    }

    @CachePut(value = "flights", key = "#id")
    public Flight deleteFlightById(Long id) {
        Flight flight = flightRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Flight not found")
        );
        flight.setDeletedAt(Timestamp.from(Instant.now()));
        return flightRepository.save(flight);
    }

    @CachePut(value = "flights", key = "#id")
    public Flight restoreById(Long id) {
        Flight flight = flightRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Flight not found")
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
