package pl.sgorski.AirLink.controller.graphql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import pl.sgorski.AirLink.containers_config.BaseIT;
import pl.sgorski.AirLink.dto.FlightResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureGraphQlTester
public class FlightResolverIT extends BaseIT {

    @Autowired
    private GraphQlTester tester;

    private FlightResponse activeFlight;
    private FlightResponse deprecatedFlight;

    @BeforeEach
    void setUp() {
        activeFlight = new FlightResponse();
        activeFlight.setId(2L);
        activeFlight.setFrom("City A");
        activeFlight.setTo("City B");
        activeFlight.setAirplaneName("Boeing 777");
        activeFlight.setPrice(100.0);
        activeFlight.setDeparture(LocalDateTime.of(2120, 1, 1, 0, 0));
        activeFlight.setArrival(LocalDateTime.of(2120, 1, 1, 3, 0));

        deprecatedFlight = new FlightResponse();
        deprecatedFlight.setId(1L);
        deprecatedFlight.setFrom("City A");
        deprecatedFlight.setTo("City B");
        deprecatedFlight.setAirplaneName("Boeing 777");
        deprecatedFlight.setPrice(100.0);
        deprecatedFlight.setDeparture(LocalDateTime.of(2020, 1, 1, 0, 0));
        deprecatedFlight.setArrival(LocalDateTime.of(2020, 1, 1, 3, 0));
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldReturnFlights_OnlyActive_User() {
        String query = """
                    query {
                        flights(pageInput: {page: 1, size: 10}) {
                            id
                            from
                            to
                            price
                            departure
                            arrival
                            airplaneName
                        }
                    }
                """;

        tester.document(query)
                .execute()
                .path("flights").entityList(FlightResponse.class).hasSize(1)
                .contains(activeFlight);
    }

    @Test
    @WithAnonymousUser
    void shouldReturnFlights_OnlyActive_Anonymous() {
        String query = """
                    query {
                        flights(pageInput: {page: 1, size: 10}) {
                            id
                            from
                            to
                            price
                            departure
                            arrival
                            airplaneName
                        }
                    }
                """;

        tester.document(query)
                .execute()
                .path("flights").entityList(FlightResponse.class).hasSize(1)
                .contains(activeFlight);
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldReturnFlightById() {
        String query = """
                    query {
                        flight(id: 2) {
                            id
                        }
                    }
                """;

        tester.document(query)
                .execute()
                .path("flight.id").entity(Long.class).isEqualTo(2L);
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldNotReturnFlightById_Deprecated() {
        String query = """
                    query {
                        flight(id: 1) {
                            id
                        }
                    }
                """;

        tester.document(query)
                .execute()
                .errors()
                .satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldReturnFlightById_DeprecatedButAdmin() {
        String query = """
                    query {
                        flight(id: 1) {
                            id
                            from
                            to
                            price
                            departure
                            arrival
                            airplaneName
                        }
                    }
                """;

        tester.document(query)
                .execute()
                .path("flight").entity(FlightResponse.class).isEqualTo(deprecatedFlight);
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldDeleteFlight() {
        String mutation = """
                    mutation DeleteFlight {
                        deleteFlight(id: 2) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .path("deleteFlight.id").entity(Long.class).isEqualTo(2L);
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldNotDeleteFlight_NotActive() {
        String mutation = """
                    mutation DeleteFlight {
                        deleteFlight(id: 1) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldNotDeleteFlight_AlreadyDeleted() {
        String mutation = """
                    mutation DeleteFlight {
                        deleteFlight(id: 3) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithAnonymousUser
    void shouldNotDeleteFlight_AnonymousUser() {
        String mutation = """
                    mutation DeleteFlight {
                        deleteFlight(id: 1) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldRestoreFlight() {
        String mutation = """
                    mutation RestoreFlight {
                        restoreFlight(id: 3) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .path("restoreFlight.id").entity(Long.class).isEqualTo(3L);
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldNotRestoreFlight_NotDeleted() {
        String mutation = """
                    mutation RestoreFlight {
                        restoreFlight(id: 1) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldNotRestoreFlight_NotActive() {
        String mutation = """
                    mutation RestoreFlight {
                        restoreFlight(id: 2) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithAnonymousUser
    void shouldNotRestoreFlight_AnonymousUser() {
        String mutation = """
                    mutation RestoreFlight {
                        restoreFlight(id: 3) {
                            id
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldCreateFlight() {
        String mutation = """
                mutation {
                    createFlight(
                        flightRequest: {
                            fromAirportId: 2,
                            toAirportId: 1,
                            airplaneId: 1,
                            price: 150.0,
                            departure: "2123-01-01T10:00:00",
                            arrival: "2123-01-01T12:00:00"
                        }
                    ) {
                        id
                        from
                        to
                        departure
                        arrival
                        price
                        airplaneName
                        createdAt
                    }
                }
                """;

        tester.document(mutation)
                .execute()
                .path("createFlight.id").entity(Long.class)
                .path("createFlight.from").entity(String.class).isEqualTo("City B")
                .path("createFlight.to").entity(String.class).isEqualTo("City A")
                .path("createFlight.price").entity(Double.class).isEqualTo(150.0);
    }

    @Test
    @WithUserDetails("test@admin.com")
    void shouldUpdateFlight() {
        String mutation = """
                mutation {
                    updateFlight(
                        id: 2,
                        flightRequest: {
                            fromAirportId: 2,
                            toAirportId: 1,
                            airplaneId: 1,
                            price: 150.0,
                            departure: "2120-01-01T20:00:00",
                            arrival: "2120-01-01T21:00:00"
                        }
                    ) {
                        id
                        from
                        to
                        departure
                        arrival
                        price
                        airplaneName
                        createdAt
                    }
                }
                """;

        tester.document(mutation)
                .execute()
                .path("updateFlight.id").entity(Long.class)
                .path("updateFlight.from").entity(String.class).isEqualTo("City B")
                .path("updateFlight.to").entity(String.class).isEqualTo("City A")
                .path("updateFlight.price").entity(Double.class).isEqualTo(150.0);
    }
}
