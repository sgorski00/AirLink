package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationTests {

    @Test
    void defaultStatusShouldBePending() {
        Reservation reservation = new Reservation();
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }
}
