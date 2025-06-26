package pl.sgorski.AirLink.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sgorski.AirLink.dto.AirportRequest;
import pl.sgorski.AirLink.dto.AirportResponse;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.model.localization.City;
import pl.sgorski.AirLink.service.localization.CityService;

@Mapper(componentModel = "spring")
public abstract class AirportMapper {

    @Autowired
    protected CityService cityService;

    @Mapping(target = "country", source = "city.country.name")
    @Mapping(target = "city", source = "city.name")
    public abstract AirportResponse toResponse(Airport airport);

    @Mapping(target = "city", source = "cityId", qualifiedByName = "cityById")
    public abstract Airport toAirport(AirportRequest airportRequest);

    @Named("cityById")
    protected City mapCity(Long cityId) {
        return cityService.findById(cityId);
    }
}
