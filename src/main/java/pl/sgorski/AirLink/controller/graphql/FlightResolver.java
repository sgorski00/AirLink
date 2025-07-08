package pl.sgorski.AirLink.controller.graphql;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sgorski.AirLink.dto.FlightRequest;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.dto.generic.PageInput;
import pl.sgorski.AirLink.dto.generic.SortInput;
import pl.sgorski.AirLink.mapper.FlightMapper;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.service.FlightService;

@Controller
@RequiredArgsConstructor
public class FlightResolver {

    private final FlightService flightService;
    private final FlightMapper flightMapper;

    @QueryMapping("flights")
    public Page<FlightResponse> getFlights(
            @Argument PageInput pageInput,
            @Argument SortInput sortInput,
            @Argument Long airportFrom,
            @Argument Long airportTo
    ) {
        Sort sort = sortInput.toSort();
        PageRequest pageRequest = pageInput.toPageRequest(sort);
        Page<Flight> flightsPage = flightService.findAllActivePaginated(pageRequest, airportFrom, airportTo);
        return flightsPage.map(flightMapper::toResponse);
    }

    @QueryMapping("flight")
    public FlightResponse getFlightById(@Argument @NotNull Long id) {
        Flight flight = flightService.findById(id);
        return flightMapper.toResponse(flight);
    }

    @MutationMapping("deleteFlight")
    @PreAuthorize("hasAuthority('ADMIN')")
    public FlightResponse deleteFlight(@Argument @NotNull Long id) {
        Flight flight = flightService.deleteById(id);
        return flightMapper.toResponse(flight);
    }

    @MutationMapping("restoreFlight")
    @PreAuthorize("hasAuthority('ADMIN')")
    public FlightResponse restoreFlight(@Argument @NotNull Long id) {
        Flight flight = flightService.restoreById(id);
        return flightMapper.toResponse(flight);
    }

    @MutationMapping("createFlight")
    @PreAuthorize("hasAuthority('ADMIN')")
    public FlightResponse createFlight(
            @Argument @Valid FlightRequest flightRequest
    ) {
        Flight flight = flightMapper.toFlight(flightRequest);
        flight = flightService.save(flight);
        return flightMapper.toResponse(flight);
    }

    @MutationMapping("updateFlight")
    @PreAuthorize("hasAuthority('ADMIN')")
    public FlightResponse updateFlight(
            @Argument @NotNull Long id,
            @Argument @Valid FlightRequest flightRequest
    ) {
        Flight existingFlight = flightService.findById(id);
        flightMapper.updateFlight(flightRequest, existingFlight);
        Flight flight = flightService.save(existingFlight);
        return flightMapper.toResponse(flight);
    }
}
