package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.model.Flight;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTests {

    @Mock
    private FlightCacheService flightCacheService;

    @Mock
    private AirplaneService airplaneService;

    @InjectMocks
    private FlightService flightService;

    @Mock
    private Airplane airplane;

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setAirplane(airplane);
        flight.setDeparture(LocalDateTime.now().plusDays(1));
        flight.setArrival(LocalDateTime.now().plusHours(2));
    }

    @Test
    void shouldSave() {
        when(airplane.isAvailable(any(Flight.class))).thenReturn(true);
        when(airplaneService.findByIdWithFlights(anyLong())).thenReturn(airplane);

        flightService.save(flight);

        verify(airplaneService, times(1)).findByIdWithFlights(anyLong());
        verify(flightCacheService, times(1)).save(flight);
    }

    @Test
    void shouldThrowIfAirplaneIsNotAvailable() {
        when(airplaneService.findByIdWithFlights(anyLong())).thenReturn(airplane);
        when(airplane.isAvailable(any(Flight.class))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> flightService.save(flight));

        verify(airplaneService, times(1)).findByIdWithFlights(anyLong());
        verify(flightCacheService, never()).save(flight);
    }

    @Test
    void shouldThrowIfAirplaneNotFound() {
        when(airplaneService.findByIdWithFlights(anyLong())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> flightService.save(flight));

        verify(airplaneService, times(1)).findByIdWithFlights(anyLong());
        verify(flightCacheService, never()).save(flight);
    }

    @Test
    void shouldFindAll() {
        List<Flight> flights = List.of(flight);
        when(flightCacheService.findAll()).thenReturn(flights);

        List<Flight> result = flightService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight, result.getFirst());
        verify(flightCacheService, times(1)).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        Page<Flight> page = new PageImpl<>(List.of(flight));
        when(flightCacheService.findAllActiveFiltered(any(Pageable.class), anyLong(), anyLong())).thenReturn(page);

        Page<Flight> result = flightService.findAllActivePaginated(Pageable.unpaged(), 1L, 2L);

        assertNotNull(result);
        verify(flightCacheService, times(1)).findAllActiveFiltered(any(Pageable.class), anyLong(), anyLong());
    }

    @Test
    void shouldFindById() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(flightCacheService.findById(anyLong())).thenReturn(flight);

        Flight foundFlight = flightService.findById(1L);

        assertNotNull(foundFlight);
        assertEquals(flight, foundFlight);
        verify(flightCacheService, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowIfFlightNotFound() {
        when(flightCacheService.findById(anyLong())).thenThrow(new NoSuchElementException("Flight not found"));

        assertThrows(NoSuchElementException.class, () -> flightService.findById(1L));

        verify(flightCacheService, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowIfFlightIsNotActive() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        flight.setDeletedAt(Timestamp.from(Instant.now()));
        when(flightCacheService.findById(anyLong())).thenReturn(flight);

        assertThrows(NoSuchElementException.class, () -> flightService.findById(1L));

        verify(flightCacheService, times(1)).findById(anyLong());
    }

    @Test
    void shouldCountFlights() {
        when(flightCacheService.count()).thenReturn(10L);

        long count = flightService.count();

        assertEquals(10L, count);
        verify(flightCacheService, times(1)).count();
    }

    @Test
    void shouldMakeSoftDelete() {
        when(flightCacheService.findById(anyLong())).thenReturn(flight);
        flightService.deleteById(1L);

        assertNotNull(flight.getDeletedAt());
        verify(flightCacheService, times(1)).findById(anyLong());
        verify(flightCacheService, times(1)).save(flight);
    }

    @Test
    void shouldNotMakeSoftDeleteIfFlightNotFound() {
        when(flightCacheService.findById(anyLong())).thenThrow(new NoSuchElementException("Flight not found"));

        assertThrows(NoSuchElementException.class, () -> flightService.deleteById(1L));
        assertNull(flight.getDeletedAt());

        verify(flightCacheService, times(1)).findById(anyLong());
        verify(flightCacheService, never()).save(flight);
    }

    @Test
    void shouldNotMakeSoftDeleteAfterDeparture() {
        flight.setDeparture(LocalDateTime.now().minusDays(1));
        when(flightCacheService.findById(anyLong())).thenReturn(flight);

        assertThrows(IllegalArgumentException.class, () -> flightService.deleteById(1L));
        assertNull(flight.getDeletedAt());

        verify(flightCacheService, times(1)).findById(anyLong());
        verify(flightCacheService, times(0)).save(flight);
    }

    @Test
    void shouldRestoreSoftDelete() {
        flight.setDeletedAt(Timestamp.from(Instant.now()));
        when(flightCacheService.findDeletedById(anyLong())).thenReturn(flight);
        flightService.restoreById(1L);

        verify(flightCacheService, times(1)).findDeletedById(anyLong());
        verify(flightCacheService, times(1)).save(flight);
        assertNull(flight.getDeletedAt());
    }

    @Test
    void shouldNotRestoreIfFlightNotFound() {
        when(flightCacheService.findDeletedById(anyLong())).thenThrow(new NoSuchElementException("Flight not found or not deleted"));

        assertThrows(NoSuchElementException.class, () -> flightService.restoreById(1L));

        verify(flightCacheService, times(1)).findDeletedById(anyLong());
        verify(flightCacheService, never()).save(flight);
    }

    @Test
    void shouldFindFlightWithReservations() {
        when(flightCacheService.findByIdWithReservations(anyLong())).thenReturn(flight);

        Flight foundFlight = flightService.findByIdWithReservations(1L);

        assertNotNull(foundFlight);
        assertEquals(flight, foundFlight);
        verify(flightCacheService, times(1)).findByIdWithReservations(anyLong());
    }

    @Test
    void shouldThrowIfFlightWithReservationsNotFound() {
        when(flightCacheService.findByIdWithReservations(anyLong())).thenThrow(new NoSuchElementException("Flight not found"));

        assertThrows(NoSuchElementException.class, () -> flightService.findByIdWithReservations(1L));

        verify(flightCacheService, times(1)).findByIdWithReservations(anyLong());
    }
}
