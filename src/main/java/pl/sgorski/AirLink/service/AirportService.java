package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.model.localization.Country;
import pl.sgorski.AirLink.repository.AirportRepository;
import pl.sgorski.AirLink.service.localization.CountryService;

import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final CountryService countryService;

    public Airport save(Airport airport) {
        return airportRepository.save(airport);
    }

    public Page<Airport> findAll(Long countryId, Pageable pageable) {
        if(countryId != null) {
            log.debug("Finding airports for country with id: {}", countryId);
            Country country = countryService.findById(countryId);
            return airportRepository.findAllByCountry(country, pageable);
        }
        log.debug("Finding all airports");
        return airportRepository.findAll(pageable);
    }

    public Airport findById(Long id) {
        log.debug("Finding airport with id: {}", id);
        return airportRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Airport with id " + id + " not found")
        );
    }
}
