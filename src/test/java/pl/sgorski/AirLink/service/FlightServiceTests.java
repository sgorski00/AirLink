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
import pl.sgorski.AirLink.repository.FlightRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTests {

    @Mock
    private FlightRepository flightRepository;

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
        when(airplane.isAvailable(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);
        when(airplaneService.findByIdWithFlights(anyLong())).thenReturn(airplane);

        flightService.save(flight);

        verify(airplaneService, times(1)).findByIdWithFlights(anyLong());
        verify(flightRepository, times(1)).save(flight);
    }

    @Test
    void shouldThrowIfAirplaneIsNotAvailable() {
        when(airplaneService.findByIdWithFlights(anyLong())).thenReturn(airplane);
        when(airplane.isAvailable(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> flightService.save(flight));

        verify(airplaneService, times(1)).findByIdWithFlights(anyLong());
        verify(flightRepository, never()).save(flight);
    }

    @Test
    void shouldThrowIfAirplaneNotFound() {
        when(airplaneService.findByIdWithFlights(anyLong())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> flightService.save(flight));

        verify(airplaneService, times(1)).findByIdWithFlights(anyLong());
        verify(flightRepository, never()).save(flight);
    }

    @Test
    void shouldFindAll() {
        List<Flight> flights = List.of(flight);
        when(flightRepository.findAll()).thenReturn(flights);

        List<Flight> result = flightService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flight, result.getFirst());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        Page<Flight> page = new PageImpl<>(List.of(flight));
        when(flightRepository.findAllActiveFiltered(any(Pageable.class), anyLong(), anyLong())).thenReturn(page);

        Page<Flight> result = flightService.findAllActivePaginated(Pageable.unpaged(), 1L, 2L);

        assertNotNull(result);
        verify(flightRepository, times(1)).findAllActiveFiltered(any(Pageable.class), anyLong(), anyLong());
    }

    @Test
    void shouldFindById() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(flightRepository.findById(anyLong())).thenReturn(Optional.of(flight));

        Flight foundFlight = flightService.findById(1L);

        assertNotNull(foundFlight);
        assertEquals(flight, foundFlight);
        verify(flightRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowIfFlightNotFound() {
        assertThrows(NoSuchElementException.class, () -> flightService.findById(1L));

        verify(flightRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowIfFlightIsNotActive() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        flight.setDeletedAt(Timestamp.from(Instant.now()));
        when(flightRepository.findById(anyLong())).thenReturn(Optional.of(flight));

        assertThrows(NoSuchElementException.class, () -> flightService.findById(1L));

        verify(flightRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldCountFlights() {
        when(flightRepository.count()).thenReturn(10L);

        long count = flightService.count();

        assertEquals(10L, count);
        verify(flightRepository, times(1)).count();
    }

    @Test
    void shouldMakeSoftDelete() {
        when(flightRepository.findById(anyLong())).thenReturn(Optional.of(flight));
        flightService.deleteFlightById(1L);

        assertNotNull(flight.getDeletedAt());
        verify(flightRepository, times(1)).findById(anyLong());
        verify(flightRepository, times(1)).save(flight);
    }

    @Test
    void shouldNotMakeSoftDeleteIfFlightNotFound() {
        when(flightRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> flightService.deleteFlightById(1L));
        assertNull(flight.getDeletedAt());

        verify(flightRepository, times(1)).findById(anyLong());
        verify(flightRepository, never()).save(flight);
    }

    @Test
    void shouldNotMakeSoftDeleteAfterDeparture() {
        flight.setDeparture(LocalDateTime.now().minusDays(1));
        when(flightRepository.findById(anyLong())).thenReturn(Optional.of(flight));

        assertThrows(IllegalArgumentException.class, () -> flightService.deleteFlightById(1L));
        assertNull(flight.getDeletedAt());

        verify(flightRepository, times(1)).findById(anyLong());
        verify(flightRepository, times(0)).save(flight);
    }

    @Test
    void shouldRestoreSoftDelete() {
        flight.setDeletedAt(Timestamp.from(Instant.now()));
        when(flightRepository.findDeletedById(anyLong())).thenReturn(Optional.of(flight));
        flightService.restoreById(1L);

        verify(flightRepository, times(1)).findDeletedById(anyLong());
        verify(flightRepository, times(1)).save(flight);
        assertNull(flight.getDeletedAt());
    }

    @Test
    void shouldNotRestoreIfFlightNotFound() {
        when(flightRepository.findDeletedById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> flightService.restoreById(1L));

        verify(flightRepository, times(1)).findDeletedById(anyLong());
        verify(flightRepository, never()).save(flight);
    }

    @Test
    void shouldFindFlightWithReservations() {
        when(flightRepository.findByIdWithReservations(anyLong())).thenReturn(Optional.of(flight));

        Flight foundFlight = flightService.findByIdWithReservations(1L);

        assertNotNull(foundFlight);
        assertEquals(flight, foundFlight);
        verify(flightRepository, times(1)).findByIdWithReservations(anyLong());
    }

    @Test
    void shouldThrowIfFlightWithReservationsNotFound() {
        when(flightRepository.findByIdWithReservations(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> flightService.findByIdWithReservations(1L));

        verify(flightRepository, times(1)).findByIdWithReservations(anyLong());
    }
}
