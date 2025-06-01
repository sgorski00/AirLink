package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.ReservationStatus;
import pl.sgorski.AirLink.repository.ReservationRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTests {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private Flight flight;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setFlight(flight);
        reservation.setNumberOfSeats(2);
    }

    @Test
    void shouldSaveReservation() {
        when(flight.isAvailableToBook(anyInt())).thenReturn(true);

        reservationService.save(reservation);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldNotSaveReservationIfFlightHasNoAvailableSeats() {
        when(flight.isAvailableToBook(anyInt())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> reservationService.save(reservation));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldFindReservationById() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        reservationService.findById(1L);

        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowIfReservationByIdNotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.findById(1L));

        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldSoftDeleteById() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        reservationService.deleteById(1L);

        assertNotNull(reservation.getDeletedAt());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowWhileSoftDeletingByIdIfNotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.deleteById(1L));

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldRestoreReservationById() {
        reservation.setDeletedAt(Timestamp.from(Instant.now()));
        when(reservationRepository.findDeletedById(anyLong())).thenReturn(Optional.of(reservation));

        reservationService.restoreById(1L);

        assertNull(reservation.getDeletedAt());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(reservationRepository, times(1)).findDeletedById(1L);
    }

    @Test
    void shouldThrowWhileRestoringByIdIfNotFound() {
        when(reservationRepository.findDeletedById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.restoreById(1L));

        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(reservationRepository, times(1)).findDeletedById(1L);
    }

    @Test
    void shouldUpdateReservationStatus() {
        UpdateReservationRequest updateRequest = new UpdateReservationRequest();
        updateRequest.setStatus("CONFIRMED");
        reservation.setStatus(ReservationStatus.PENDING);
        when(flight.isAvailableToBook(anyInt())).thenReturn(true);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation saved = reservationService.updateReservationById(1L, updateRequest);

        assertEquals(ReservationStatus.CONFIRMED, saved.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldThrowWhenWrongStatusPassed() {
        UpdateReservationRequest updateRequest = new UpdateReservationRequest();
        updateRequest.setStatus("NOTEXISTS");
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        assertThrows(IllegalArgumentException.class, () -> reservationService.updateReservationById(1L, updateRequest));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldThrowWhenNullStatusPassed() {
        UpdateReservationRequest updateRequest = new UpdateReservationRequest();
        updateRequest.setStatus(null);
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        assertThrows(NullPointerException.class, () -> reservationService.updateReservationById(1L, updateRequest));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
