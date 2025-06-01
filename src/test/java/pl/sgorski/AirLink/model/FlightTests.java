package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlightTests {

    private Flight flight;
    private final List<Reservation> reservations = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 5; i++) {
            Reservation reservation = new Reservation();
            reservation.setNumberOfSeats(5);
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservations.add(reservation);
        }

        for (int i = 0; i < 5; i++) {
            Reservation reservation = new Reservation();
            reservation.setNumberOfSeats(5);
            reservation.setStatus(ReservationStatus.PENDING);
            reservations.add(reservation);
        }

        Airplane airplane = new Airplane();
        airplane.setSeats(150);

        flight = new Flight();
        flight.setAirplane(airplane);
        flight.setReservations(reservations);
    }

    @Test
    void shouldReturnAvailableSeats() {
        assertEquals(100, flight.getAvailableSeats());
    }

    @Test
    void shouldReturnTrueIfFlightHasAvailableSeats() {
        assertTrue(flight.hasAvailableSeats(1));
    }

    @Test
    void shouldReturnFalseIfFlightHasNotAvailableSeats() {
        assertFalse(flight.hasAvailableSeats(9999));
    }

    @Test
    void shouldReturnAvailableSeatsEqualsToAirplaneTotalSeatsIfNoReservations() {
        flight.setReservations(new ArrayList<>());
        assertEquals(150, flight.getAvailableSeats());
    }

    @Test
    void shouldReturnAvailableSeatsEqualsToAirplaneTotalSeatsIfNullReservations() {
        flight.setReservations(null);
        assertEquals(150, flight.getAvailableSeats());
    }

    @Test
    void shouldNotTakeAvailableSeatsIfReservationHasStatusCancelled() {
        Reservation res = new Reservation();
        res.setNumberOfSeats(5);
        res.setStatus(ReservationStatus.CANCELLED);

        flight.getReservations().add(res);

        assertEquals(100, flight.getAvailableSeats());
    }

    @Test
    void shouldNotTakeAvailableSeatsIfReservationHasStatusCompleted() {
        Reservation res = new Reservation();
        res.setNumberOfSeats(5);
        res.setStatus(ReservationStatus.COMPLETED);

        flight.getReservations().add(res);

        assertEquals(100, flight.getAvailableSeats());
    }

    @Test
    void shouldReturnTrueIfFlightIsAvailableToBook() {
        flight.setDeparture(LocalDateTime.now().plusDays(1));
        flight.setReservations(new ArrayList<>());
        assertTrue(flight.isAvailableToBook(1));
    }

    @Test
    void shouldReturnFalseIfFlightIsNotAvailableToBook() {
        flight.setDeparture(LocalDateTime.now().minusDays(1));
        flight.setReservations(new ArrayList<>());
        assertFalse(flight.isAvailableToBook(1));
    }

    @Test
    void shouldReturnFalseIfFlightIsDeleted() {
        flight.setDeparture(LocalDateTime.now().plusDays(1));
        flight.setDeletedAt(Timestamp.from(Instant.now()));
        flight.setReservations(new ArrayList<>());
        assertFalse(flight.isAvailableToBook(1));
    }
}
