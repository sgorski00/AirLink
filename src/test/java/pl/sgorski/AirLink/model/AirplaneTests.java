package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AirplaneTests {

    private List<Flight> flights;

    @BeforeEach
    void setUp() {
        Flight flight1 = new Flight();
        flight1.setDeparture(LocalDateTime.now().minusDays(1).withMinute(0).withSecond(0).withNano(0));
        flight1.setArrival(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0));
        Flight flight2 = new Flight();
        flight2.setDeparture(LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0));
        flight2.setArrival(LocalDateTime.now().plusDays(3).withSecond(0).withMinute(0).withNano(0));
        flights = List.of(flight1, flight2);
    }

    @Test
    void shouldReturnTrueIfAvailable() {
        Airplane airplane = new Airplane();
        airplane.setFlights(flights);

        boolean isAvailable = airplane.isAvailable(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5));

        assertTrue(isAvailable);
    }

    @Test
    void shouldReturnFalseIfNotAvailable_StartBefore_EndInside() {
        Airplane airplane = new Airplane();
        airplane.setFlights(flights);

        boolean isAvailable = airplane.isAvailable(
                LocalDateTime.now().minusDays(2).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0)
        );

        assertFalse(isAvailable);
    }

    @Test
    void shouldReturnFalseIfNotAvailable_StartBefore_EndAfter() {
        Airplane airplane = new Airplane();
        airplane.setFlights(flights);

        boolean isAvailable = airplane.isAvailable(
                LocalDateTime.now().minusDays(2).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(4).withMinute(0).withSecond(0).withNano(0)
        );

        assertFalse(isAvailable);
    }

    @Test
    void shouldReturnFalseIfNotAvailable_StartInside_EndAfter() {
        Airplane airplane = new Airplane();
        airplane.setFlights(flights);

        boolean isAvailable = airplane.isAvailable(
                LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(4).withMinute(0).withSecond(0).withNano(0)
        );

        assertFalse(isAvailable);
    }

    @Test
    void shouldReturnFalseIfNotAvailable_StartInside_EndInside() {
        Airplane airplane = new Airplane();
        airplane.setFlights(flights);

        boolean isAvailable = airplane.isAvailable(
                LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(3).withMinute(0).withSecond(0).withNano(0)
        );

        assertFalse(isAvailable);
    }

    @Test
    void shouldReturnTrueIfNoFlights() {
        Airplane airplane = new Airplane();
        airplane.setFlights(List.of());

        boolean isAvailable = airplane.isAvailable(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertTrue(isAvailable);
    }

    @Test
    void shouldReturnTrueIfFlightsNull() {
        Airplane airplane = new Airplane();
        airplane.setFlights(null);

        boolean isAvailable = airplane.isAvailable(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertTrue(isAvailable);
    }
}
