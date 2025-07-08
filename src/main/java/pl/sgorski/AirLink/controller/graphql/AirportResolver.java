package pl.sgorski.AirLink.controller.graphql;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sgorski.AirLink.dto.AirportRequest;
import pl.sgorski.AirLink.dto.AirportResponse;
import pl.sgorski.AirLink.dto.generic.PageInput;
import pl.sgorski.AirLink.mapper.AirportMapper;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.service.AirportService;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AirportResolver {

    private final AirportService airportService;
    private final AirportMapper airportMapper;

    @QueryMapping("airports")
    public Page<AirportResponse> getAirports(
            @Argument PageInput pageInput,
            @Argument Long countryId
    ) {
        PageRequest pageRequest = pageInput.toPageRequest(null);
        Page<Airport> airports = airportService.findAll(countryId, pageRequest);
        return airports.map(airportMapper::toResponse);
    }

    @QueryMapping("airport")
    public AirportResponse getAirport(
            @Argument Long id
    ) {
        Airport airport = airportService.findById(id);
        return airportMapper.toResponse(airport);
    }

    @MutationMapping("createAirport")
    public AirportResponse createAirport(
            @Argument("input") @Valid AirportRequest request
    ) {
        Airport airport = airportMapper.toAirport(request);
        Airport savedAirport = airportService.save(airport);
        return airportMapper.toResponse(savedAirport);
    }

    @MutationMapping("updateAirport")
    public AirportResponse updateAirport(
            @Argument Long id,
            @Argument("input") @Valid AirportRequest request
    ) {
        Airport existingAirport = airportService.findById(id);
        existingAirport.update(airportMapper.toAirport(request));
        Airport updatedAirport = airportService.save(existingAirport);
        return airportMapper.toResponse(updatedAirport);
    }
}
