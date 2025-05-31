package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.repository.AirportRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;

    public Airport save(Airport airport) {
        return airportRepository.save(airport);
    }

    public List<Airport> findAll() {
        return airportRepository.findAll();
    }

    public Airport findById(Long id) {
        log.debug("Finding airport with id: {}", id);
        return airportRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Airport with id " + id + " not found")
        );
    }
}
