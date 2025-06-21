package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import pl.sgorski.AirLink.exception.IllegalStatusException;
import pl.sgorski.AirLink.model.auth.User;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationTests {

    @Test
    void defaultStatusShouldBePending() {
        Reservation reservation = new Reservation();
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    void shouldReturnOwnerIdEqualToUserId() {
        User user = new User();
        user.setId(1L);
        Reservation reservation = new Reservation();
        reservation.setUser(user);

        long result = reservation.getOwnerId();

        assertEquals(1L, result);
    }

    @Test
    void getPriceShouldReturnCorrectValue() {
        User user = new User();
        user.setId(1L);
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setPrice(150.0);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setFlight(flight);
        reservation.setNumberOfSeats(3);

        assertEquals(450.0, reservation.getPrice());
    }

    @Test
    void toEmailContextShouldContainCorrectVariables() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@airlink.com");
        Flight flight = new Flight();
        flight.setId(2L);
        flight.setPrice(200.0);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setFlight(flight);
        reservation.setNumberOfSeats(2);

        Context context = reservation.toEmailContext();
        assertEquals("test@airlink.com", context.getVariable("email"));
        assertEquals(flight, context.getVariable("flight"));
        assertEquals(2, context.getVariable("seats"));
        assertEquals(400.0, context.getVariable("price"));
        assertEquals(ReservationStatus.PENDING, context.getVariable("status"));
    }

    @Test
    void toHistoryShouldReturnHistoryWithCorrectStatusAndReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.PENDING);

        ReservationHistory history = reservation.toHistory();

        assertEquals(reservation, history.getReservation());
        assertEquals(ReservationStatus.PENDING, history.getStatus());
    }

    @Test
    void restoreShouldSetStatusToPendingAndClearDeletedAt() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.DELETED);
        reservation.setDeletedAt(Timestamp.from(Instant.now()));
        int historySize = reservation.getHistory().size();

        reservation.restore();

        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertNull(reservation.getDeletedAt());
        assertEquals(historySize + 1, reservation.getHistory().size());
    }

    @Test
    void setStatusShouldChangeStatusAndAddHistory() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.PENDING);
        int historySize = reservation.getHistory().size();

        reservation.setStatus(ReservationStatus.CONFIRMED);

        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        assertEquals(historySize + 1, reservation.getHistory().size());
    }

    @Test
    void setStatusShouldThrowOnNull() {
        Reservation reservation = new Reservation();
        assertThrows(IllegalArgumentException.class, () -> reservation.setStatus(null));
    }

    @Test
    void setStatusShouldThrowOnPending() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CONFIRMED);
        assertThrows(IllegalStatusException.class, () -> reservation.setStatus(ReservationStatus.PENDING));
    }

    @Test
    void setStatusShouldThrowOnInvalidTransitionToConfirmed() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CANCELLED);
        assertThrows(IllegalStatusException.class, () -> reservation.setStatus(ReservationStatus.CONFIRMED));
    }

    @Test
    void setStatusShouldThrowOnInvalidTransitionToCompleted() {
        Reservation reservation = new Reservation();
        assertThrows(IllegalStatusException.class, () -> reservation.setStatus(ReservationStatus.COMPLETED));
    }
}
