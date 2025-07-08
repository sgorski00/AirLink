package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.repository.FlightRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FlightCacheService {

    private final FlightRepository flightRepository;

    @CachePut(value = "flights", key = "#flight.id")
    public Flight save(Flight flight) {
        return flightRepository.save(flight);
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Page<Flight> findAllActiveFiltered(Pageable pageable, Long airportFrom, Long airportTo) {
        return flightRepository.findAllActiveFiltered(pageable, airportFrom, airportTo);
    }

    @Cacheable(value = "flights", key = "#id")
    public Flight findById(Long id) {
        return flightRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Flight not found")
        );
    }

    public Flight findDeletedById(Long id) {
        return flightRepository.findDeletedById(id).orElseThrow(
                () -> new NoSuchElementException("Flight not found or not deleted")
        );
    }

    public Flight findByIdWithReservations(Long flightId) {
        return flightRepository.findByIdWithReservations(flightId).orElseThrow(
                () -> new NoSuchElementException("Flight with id: " + flightId + " not found")
        );
    }

    public long count() {
        return flightRepository.count();
    }
}
