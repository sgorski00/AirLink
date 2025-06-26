package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.repository.AirplaneRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AirplaneServiceTests {

    @Mock
    private AirplaneRepository airplaneRepository;

    @InjectMocks
    private AirplaneService airplaneService;

    private Airplane airplane;

    @BeforeEach
    void setUp() {
        airplane = new Airplane();
        airplane.setId(1L);
        airplane.setName("Boeing");
        airplane.setCode("BOE123");
        airplane.setSeats(100);
    }

    @Test
    void shouldSaveAirplane() {
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane);

        Airplane saved = airplaneService.save(airplane);

        assertNotNull(saved);
        assertEquals("Boeing", saved.getName());
        verify(airplaneRepository, times(1)).save(airplane);
    }

    @Test
    void shouldFindAllAirplanes() {
        when(airplaneRepository.findAll()).thenReturn(List.of(airplane));

        List<Airplane> result = airplaneService.findAll();

        assertEquals(1, result.size());
        verify(airplaneRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAllAirplanesWithPagination() {
        Page<Airplane> page = new PageImpl<>(List.of(airplane));
        when(airplaneRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Airplane> result = airplaneService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(airplaneRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldFindAirplaneById() {
        when(airplaneRepository.findById(1L)).thenReturn(Optional.of(airplane));

        Airplane found = airplaneService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(airplaneRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowIfAirplaneByIdNotFound() {
        when(airplaneRepository.findById(anyLong())).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> airplaneService.findById(2L));
        assertTrue(ex.getMessage().contains("not found"));
        verify(airplaneRepository, times(1)).findById(2L);
    }

    @Test
    void shouldFindAirplaneByIdWithFlights() {
        when(airplaneRepository.findByIdWithFlights(1L)).thenReturn(Optional.of(airplane));

        Airplane found = airplaneService.findByIdWithFlights(1L);

        assertNotNull(found);
        verify(airplaneRepository, times(1)).findByIdWithFlights(1L);
    }

    @Test
    void shouldThrowIfAirplaneByIdWithFlightsNotFound() {
        when(airplaneRepository.findByIdWithFlights(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> airplaneService.findByIdWithFlights(2L));
        verify(airplaneRepository, times(1)).findByIdWithFlights(2L);
    }

    @Test
    void shouldDeleteAirplane() {
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane);

        airplaneService.delete(airplane);

        assertNotNull(airplane.getDeletedAt());
        verify(airplaneRepository, times(1)).save(airplane);
    }

    @Test
    void shouldThrowWhenDeletingAlreadyDeletedAirplane() {
        airplane.delete();

        assertThrows(IllegalStateException.class, () -> airplaneService.delete(airplane));
        verify(airplaneRepository, never()).save(any(Airplane.class));
    }

    @Test
    void shouldRestoreAirplane() {
        airplane.delete();
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane);

        airplaneService.restore(airplane);

        assertNull(airplane.getDeletedAt());
        verify(airplaneRepository, times(1)).save(airplane);
    }

    @Test
    void shouldThrowWhenRestoringNotDeletedAirplane() {
        assertThrows(IllegalStateException.class, () -> airplaneService.restore(airplane));
        verify(airplaneRepository, never()).save(any(Airplane.class));
    }
}