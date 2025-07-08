package pl.sgorski.AirLink.mapper;

import lombok.extern.log4j.Log4j2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sgorski.AirLink.dto.FlightRequest;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.service.AirplaneService;
import pl.sgorski.AirLink.service.AirportService;

@Log4j2
@Mapper(componentModel = "spring")
public abstract class FlightMapper {

    @Autowired
    protected AirplaneService airplaneService;

    @Autowired
    protected AirportService airportService;

    @Mapping(target = "airplaneName", source = "airplane.name")
    @Mapping(target = "from", source = "from.city.name")
    @Mapping(target = "to", source = "to.city.name")
    public abstract FlightResponse toResponse(Flight flight);

    @Mapping(target = "airplane", source = "airplaneId", qualifiedByName = "airplaneById")
    @Mapping(target = "from", source = "fromAirportId", qualifiedByName = "airportById")
    @Mapping(target = "to", source = "toAirportId", qualifiedByName = "airportById")
    public abstract Flight toFlight(FlightRequest flightRequest);

    @Mapping(target = "airplane", source = "airplaneId", qualifiedByName = "airplaneById")
    @Mapping(target = "from", source = "fromAirportId", qualifiedByName = "airportById")
    @Mapping(target = "to", source = "toAirportId", qualifiedByName = "airportById")
    public abstract void updateFlight(FlightRequest flightRequest, @MappingTarget Flight flight);

    @Named("airplaneById")
    protected Airplane mapAirplane(Long id) {
        log.debug("Mapping airplane with id: {}", id);
        return airplaneService.findById(id);
    }

    @Named("airportById")
    protected Airport mapAirport(Long id) {
        log.debug("Mapping airport with id: {}", id);
        return airportService.findById(id);
    }
}
