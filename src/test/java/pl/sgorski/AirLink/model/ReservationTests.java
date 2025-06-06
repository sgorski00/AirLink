package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.Test;
import pl.sgorski.AirLink.model.auth.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
