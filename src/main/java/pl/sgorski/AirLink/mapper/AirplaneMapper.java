package pl.sgorski.AirLink.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sgorski.AirLink.dto.AirplaneRequest;
import pl.sgorski.AirLink.dto.AirplaneResponse;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.model.Flight;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = FlightMapper.class)
public abstract class AirplaneMapper {

    @Autowired
    protected FlightMapper flightMapper;

    @Mapping(target = "incomingFlight", expression = "java(mapIncomingFlight(airplane))")
    @Mapping(target = "capacity", source = "seats")
    @Mapping(target = "serialNumber", source = "serialNumber")
    public abstract AirplaneResponse toResponse(Airplane airplane);

    @Mapping(target = "seats", source = "capacity")
    @Mapping(target = "serialNumber", source = "serialNumber")
    public abstract Airplane toAirplane(AirplaneRequest airplane);

    protected FlightResponse mapIncomingFlight(Airplane airplane) {
        List<Flight> flights = airplane.getFlights();
        if (flights == null) return null;
        return flights.stream()
                .filter(f -> f.getDeletedAt() == null && f.getDeparture().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Flight::getDeparture))
                .map(flightMapper::toResponse)
                .orElse(null);
    }
}
