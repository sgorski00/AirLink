package pl.sgorski.AirLink.controller.graphql;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sgorski.AirLink.dto.AirplaneRequest;
import pl.sgorski.AirLink.dto.AirplaneResponse;
import pl.sgorski.AirLink.dto.generic.PageInput;
import pl.sgorski.AirLink.mapper.AirplaneMapper;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.service.AirplaneService;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AirplaneResolver {

    private final AirplaneService airplaneService;
    private final AirplaneMapper airplaneMapper;

    @QueryMapping("airplanes")
    public Page<AirplaneResponse> getAirplanes(
            @Argument PageInput pageInput
    ) {
        PageRequest pageRequest = pageInput.toPageRequest(null);
        return airplaneService.findAll(pageRequest)
                .map(airplaneMapper::toResponse);
    }

    @QueryMapping("airplane")
    public AirplaneResponse getAirplane(
            @Argument Long id
    ) {
        Airplane airplane = airplaneService.findById(id);
        return airplaneMapper.toResponse(airplane);
    }

    @MutationMapping("createAirplane")
    public AirplaneResponse createAirplane(
            @Argument @Valid AirplaneRequest request
    ) {
        Airplane airplane = airplaneMapper.toAirplane(request);
        Airplane savedAirplane = airplaneService.save(airplane);
        return airplaneMapper.toResponse(savedAirplane);
    }

    @MutationMapping("updateAirplane")
    public AirplaneResponse updateAirplane(
            @Argument Long id,
            @Argument @Valid AirplaneRequest request
    ) {
        Airplane existingAirplane = airplaneService.findById(id);
        existingAirplane.update(request);
        Airplane savedAirplane = airplaneService.save(existingAirplane);
        return airplaneMapper.toResponse(savedAirplane);
    }

    @MutationMapping("deleteAirplane")
    public AirplaneResponse deleteAirplane(
            @Argument Long id
    ) {
        Airplane airplane = airplaneService.findById(id);
        airplaneService.delete(airplane);
        return airplaneMapper.toResponse(airplane);
    }

    @MutationMapping("restoreAirplane")
    public AirplaneResponse restoreAirplane(
            @Argument Long id
    ) {
        Airplane airplane = airplaneService.findByIdWithDeleted(id);
        airplaneService.restore(airplane);
        return airplaneMapper.toResponse(airplane);
    }
}
