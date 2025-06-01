package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.repository.FlightRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

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
        flight.setDeparture(LocalDateTime.now());
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
    void
    shouldThrowIfAirplaneIsNotAvailable() {
        when(airplaneService.findByIdWithFlights(anyLong())).thenReturn(airplane);
        when(airplane.isAvailable(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            flightService.save(flight);
        });

        verify(airplaneService, times(1)).findByIdWithFlights(anyLong());
        verify(flightRepository, never()).save(flight);
    }

    @Test
    void shouldMakeSoftDelete() {
        when(flightRepository.findById(anyLong())).thenReturn(Optional.of(flight));
        flightService.deleteFlightById(1L);

        verify(flightRepository, times(1)).findById(anyLong());
        verify(flightRepository, times(1)).save(flight);
        assertNotNull(flight.getDeletedAt());
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
}
