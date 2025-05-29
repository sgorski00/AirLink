package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.mapper.FlightMapper;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.repository.FlightRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @CachePut(value = "flights", key = "#flight.id")
    public Flight save(Flight flight) {
        return flightRepository.save(flight);
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public List<FlightResponse> findAllResponses(){
        List<Flight> allFlights = findAll();
        return allFlights.stream()
                .map(flightMapper::toResponse).
                toList();
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
}
