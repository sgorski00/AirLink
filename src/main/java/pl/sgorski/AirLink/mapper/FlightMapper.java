package pl.sgorski.AirLink.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.model.Flight;

@Mapper(componentModel = "spring")
public interface FlightMapper {

    @Mapping(target = "airplaneName", source = "airplane.name")
    @Mapping(target = "from", source = "from.city.name")
    @Mapping(target = "to", source = "to.city.name")
    FlightResponse toResponse(Flight flight);
}
