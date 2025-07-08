package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.repository.FlightRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightCacheServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightCacheService flightCacheService;

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setId(1L);
    }

    @Test
    void shouldSaveFlight() {
        when(flightRepository.save(flight)).thenReturn(flight);

        Flight result = flightCacheService.save(flight);

        assertNotNull(result);
        verify(flightRepository, times(1)).save(flight);
    }

    @Test
    void shouldFindFlightById() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        Flight result = flightCacheService.findById(1L);

        assertNotNull(result);
        verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenFlightNotFound() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            flightCacheService.findById(1L);
        });

        verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    void shouldFindDeletedFlightById() {
        when(flightRepository.findDeletedById(1L)).thenReturn(Optional.of(flight));

        Flight result = flightCacheService.findDeletedById(1L);

        assertNotNull(result);
        verify(flightRepository, times(1)).findDeletedById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletedFlightNotFound() {
        when(flightRepository.findDeletedById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            flightCacheService.findDeletedById(1L);
        });

        verify(flightRepository, times(1)).findDeletedById(1L);
    }

    @Test
    void shouldFindAllFlights() {
        when(flightRepository.findAll()).thenReturn(List.of(flight));

        List<Flight> result = flightCacheService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAllActiveFlightsPaginated() {
        when(flightRepository.findAllActiveFiltered(any(Pageable.class), anyLong(), anyLong())).thenReturn(new PageImpl<>(List.of(flight)));

        Page<Flight> result = flightCacheService.findAllActiveFiltered(Pageable.unpaged(), 1L, 2L);

        assertNotNull(result);
        verify(flightRepository, times(1)).findAllActiveFiltered(any(Pageable.class), anyLong(), anyLong());
    }

    @Test
    void shouldFindFlightByIdWithReservations() {
        when(flightRepository.findByIdWithReservations(1L)).thenReturn(Optional.of(flight));

        Flight result = flightCacheService.findByIdWithReservations(1L);

        assertNotNull(result);
        verify(flightRepository, times(1)).findByIdWithReservations(1L);
    }

    @Test
    void shouldThrowExceptionWhenFlightWithReservationsNotFound() {
        when(flightRepository.findByIdWithReservations(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            flightCacheService.findByIdWithReservations(1L);
        });

        verify(flightRepository, times(1)).findByIdWithReservations(1L);
    }

    @Test
    void shouldCountFlights() {
        when(flightRepository.count()).thenReturn(10L);

        long count = flightCacheService.count();

        assertEquals(10L, count);
        verify(flightRepository, times(1)).count();
    }
}
