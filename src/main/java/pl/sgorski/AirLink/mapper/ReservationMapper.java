package pl.sgorski.AirLink.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.dto.NewReservationRequest;
import pl.sgorski.AirLink.dto.ReservationResponse;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.service.FlightService;

@Mapper(componentModel = "spring", uses = FlightMapper.class)
public abstract class ReservationMapper {

    @Autowired
    protected FlightMapper flightMapper;

    @Autowired
    protected FlightService flightService;

    @Mapping(target = "user", source = "user.email")
    @Mapping(target = "flight", source = "flight", qualifiedByName = "flightResponse")
    public abstract ReservationResponse toResponse(Reservation reservation);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "flight", source = "flightId", qualifiedByName = "flightById")
    public abstract Reservation toReservation(NewReservationRequest request);

    @Named("flightResponse")
    protected FlightResponse flightResponse(Flight flight) {
        return flightMapper.toResponse(flight);
    }

    @Named("flightById")
    protected Flight mapFlight(Long flightId) {
        return flightService.findByIdWithReservations(flightId);
    }

    @AfterMapping
    protected void setTotalPrice(Reservation reservation, @MappingTarget ReservationResponse response) {
        response.setTotalPrice(reservation.getPrice());
    }
}