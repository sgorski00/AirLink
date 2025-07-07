package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.model.localization.Country;
import pl.sgorski.AirLink.repository.AirportRepository;
import pl.sgorski.AirLink.service.localization.CountryService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AirportServiceTests {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private AirportService airportService;

    @Test
    void shouldSaveAirport() {
        Airport airport = new Airport();

        airportService.save(airport);

        verify(airportRepository, times(1)).save(any(Airport.class));
    }

    @Test
    void shouldReturnAllIfCountryNotPassed() {
        when(airportRepository.findAll(nullable(PageRequest.class))).thenReturn(Page.empty());

        airportService.findAll(null, null);

        verify(airportRepository, times(1)).findAll(nullable(PageRequest.class));
        verify(airportRepository, never()).findAllByCountry(nullable(Country.class), nullable(PageRequest.class));
        verify(countryService, never()).findById(nullable(Long.class));
    }

    @Test
    void shouldReturnFilteredPageByCountry() {
        when(countryService.findById(anyLong())).thenReturn(new Country());
        when(airportRepository.findAllByCountry(any(Country.class), nullable(PageRequest.class))).thenReturn(Page.empty());

        airportService.findAll(1L, null);

        verify(airportRepository, never()).findAll(nullable(PageRequest.class));
        verify(airportRepository, times(1)).findAllByCountry(nullable(Country.class), nullable(PageRequest.class));
        verify(countryService, times(1)).findById(nullable(Long.class));
    }

    @Test
    void shouldThrowIfCountryNotFound() {
        when(countryService.findById(anyLong())).thenThrow(new NoSuchElementException("Country not found"));

        assertThrows(NoSuchElementException.class, () -> airportService.findAll(1L, null));

        verify(airportRepository, never()).findAll(nullable(PageRequest.class));
        verify(airportRepository, never()).findAllByCountry(nullable(Country.class), nullable(PageRequest.class));
        verify(countryService, times(1)).findById(nullable(Long.class));
    }

    @Test
    void shouldReturnAirportById() {
        when(airportRepository.findById(anyLong())).thenReturn(Optional.of(new Airport()));

        Airport result = airportService.findById(1L);

        assertNotNull(result);
        verify(airportRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowWhenAirportByIdNotFound() {
        when(airportRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> airportService.findById(1L));

        verify(airportRepository, times(1)).findById(anyLong());
    }
}
