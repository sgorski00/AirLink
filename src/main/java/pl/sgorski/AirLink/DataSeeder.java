package pl.sgorski.AirLink;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sgorski.AirLink.model.*;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.model.localization.City;
import pl.sgorski.AirLink.model.localization.Country;
import pl.sgorski.AirLink.service.*;
import pl.sgorski.AirLink.service.auth.RoleService;
import pl.sgorski.AirLink.service.auth.UserService;
import pl.sgorski.AirLink.service.localization.CityService;
import pl.sgorski.AirLink.service.localization.CountryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final RoleService roleService;
    private final UserService userService;
    private final CityService cityService;
    private final CountryService countryService;
    private final FlightService flightService;
    private final AirplaneService airplaneService;
    private final AirportService airportService;
    private final ReservationService reservationService;
    private final ProfileService profileService;

    private final Faker faker = new Faker();

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Running data seeder...");
        List<Role> roles = initRoles();
        List<User> users = initUsers(roles);
        List<City> cities = initCities();
        List<Flight> flights = initFlights(cities);
        List<Reservation> reservations = initReservations(users, flights);
        log.info("Initialization complete.");
    }

    private List<Reservation> initReservations(List<User> users, List<Flight> flights) {
        if(reservationService.count() > 0) return reservationService.findAll();
        log.info("Initializing reservations...");
        List<Reservation> reservations = new ArrayList<>();

        for(int i = 0; i<200; i++) {
            Flight flight = faker.options().nextElement(flights);
            int numberOfSeats = faker.number().numberBetween(1, 5);
            if(!flight.isAvailableToBook(numberOfSeats)) continue;

            Reservation reservation = new Reservation();
            reservation.setFlight(flight);
            reservation.setUser(faker.options().nextElement(users));
            reservation.setNumberOfSeats(numberOfSeats);
            reservations.add(reservationService.save(reservation));
        }

        return reservations;
    }

    private List<Role> initRoles() {
        if (roleService.count() > 0) return roleService.findAll();
        log.info("Initializing roles...");
        List<Role> roles = new ArrayList<>();
        Role userRole = new Role();
        userRole.setName("USER");
        roles.add(roleService.save(userRole));
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roles.add(roleService.save(adminRole));
        return roles;
    }

    private List<User> initUsers(List<Role> roles) {
        if (userService.count() > 0) return userService.findAll();
        log.info("Initializing users...");
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setEmail("admin@sg.com");
        user.setPassword("password123");
        user.setRole(roles.get(1));
        Profile profile = profileService.save(new Profile());
        user.setProfile(profile);
        users.add(userService.save(user));

        for(int i = 0; i < 100; i++) {
            profile = new Profile();
            profileService.save(profile);
            user = new User();
            user.setProfile(profile);
            user.setEmail(faker.internet().emailAddress());
            user.setPassword("password123");
            user.setRole(roles.getFirst());
            users.add(userService.save(user));
        }
        return users;
    }

    private List<City> initCities() {
        if (cityService.count() > 0) return cityService.findAll();
        log.info("Initializing cities and countries...");

        List<City> cities = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            String countryName = faker.address().country();
            String countryCode = faker.address().countryCode().toUpperCase();
            String cityName = faker.address().city();

            log.info("Country: {} - {}, City: {}", countryName, countryCode, cityName);

            if (cityService.existsByName(cityName) || countryService.existsByNameOrCode(countryName, countryCode)) {
                log.info("City or country already exists: {} / {}", cityName, countryName + " - " + countryCode);
                continue;
            }

            Country country = new Country();
            country.setName(countryName);
            country.setCode(countryCode);

            country = countryService.save(country);

            City city = new City();
            city.setCountry(country);
            city.setName(cityName);
            cityService.save(city);
            cities.add(city);
        }

        return cities;
    }


    private List<Flight> initFlights(List<City> cities) {
        if (flightService.count() > 0) return flightService.findAll();
        log.info("Initializing flights...");
        List<Flight> flights = new ArrayList<>();
        List<Airplane> airplanes = new ArrayList<>();
        List<Airport> airports = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Airplane airplane = new Airplane();
            airplane.setName(faker.aviation().aircraft());
            airplane.setCode(faker.bothify("?###").toUpperCase());
            airplane.setSeats(faker.number().numberBetween(100, 500));
            airplane = airplaneService.save(airplane);
            airplanes.add(airplane);
        }

        for (int i = 0; i < 15; i++) {
            Airport airport = new Airport();
            airport.setCity(faker.options().nextElement(cities));
            airport.setIcao(faker.bothify("????"));
            airport = airportService.save(airport);
            airports.add(airport);
        }

        for (int i = 0; i < 50; i++) {
            Flight flight = new Flight();
            flight.setAirplane(faker.options().nextElement(airplanes));

            Airport from = faker.options().nextElement(airports);
            flight.setFrom(from);
            //noinspection OptionalGetWithoutIsPresent
            flight.setTo(airports.stream()
                    .filter(ap -> !ap.equals(from))
                    .findFirst()
                    .get()
            );

            LocalDateTime departure = LocalDateTime.now()
                    .withSecond(0)
                    .withNano(0)
                    .withMinute(faker.number().numberBetween(0, 60))
                    .plusHours(faker.number().numberBetween(0, 24))
                    .plusDays(faker.number().numberBetween(-100, 100));
            flight.setDeparture(departure);
            flight.setArrival(departure
                    .plusMinutes(faker.number().numberBetween(0, 60))
                    .plusHours(faker.number().numberBetween(1, 14))
            );

            flight.setPrice(faker.number().randomDouble(2, 100, 500));

            flights.add(flight);
            flightService.save(flight);
        }
        return flights;
    }
}
