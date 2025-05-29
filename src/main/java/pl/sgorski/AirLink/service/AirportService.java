package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.repository.AirportRepository;

import java.util.List;

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
}
