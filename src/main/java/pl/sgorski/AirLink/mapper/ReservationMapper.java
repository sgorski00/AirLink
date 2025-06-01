package pl.sgorski.AirLink.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.dto.ReservationRequest;
import pl.sgorski.AirLink.dto.ReservationResponse;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.service.FlightService;
import pl.sgorski.AirLink.service.auth.UserService;

@Mapper(componentModel = "spring", uses = FlightMapper.class)
public abstract class ReservationMapper {

    @Autowired
    protected FlightMapper flightMapper;

    @Autowired
    protected UserService userService;

    @Autowired
    protected FlightService flightService;

    @Mapping(target = "user", source = "user.email")
    @Mapping(target = "flight", source = "flight", qualifiedByName = "flightResponse")
    public abstract ReservationResponse toResponse(Reservation reservation);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userById")
    @Mapping(target = "flight", source = "flightId", qualifiedByName = "flightById")
    public abstract Reservation toReservation(ReservationRequest request);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userById")
    @Mapping(target = "flight", source = "flightId", qualifiedByName = "flightById")
    public abstract void updateReservation(ReservationRequest request, @MappingTarget Reservation reservation);

    @Named("flightResponse")
    protected FlightResponse flightResponse(Flight flight) {
        return flightMapper.toResponse(flight);
    }

    @Named("userById")
    protected User mapUser(Long userId) {
        return userService.findById(userId);
    }

    @Named("flightById")
    protected Flight mapFlight(Long flightId) {
        return flightService.findByIdWithReservations(flightId);
    }

    @AfterMapping
    protected void setTotalPrice(Reservation reservation, @MappingTarget ReservationResponse response) {
        response.setTotalPrice(reservation.getFlight().getPrice() * reservation.getNumberOfSeats());
    }
}