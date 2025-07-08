package pl.sgorski.AirLink.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.AirplaneRequest;
import pl.sgorski.AirLink.dto.AirplaneResponse;
import pl.sgorski.AirLink.dto.generic.PaginationResponseDto;
import pl.sgorski.AirLink.dto.generic.ResponseDto;
import pl.sgorski.AirLink.mapper.AirplaneMapper;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.service.AirplaneService;

@RestController
@RequestMapping("/api/airplanes")
@RequiredArgsConstructor
@Tag(name = "Airplanes", description = "Endpoints for managing airplanes")
public class AirplaneController {

    private final AirplaneService airplaneService;
    private final AirplaneMapper airplaneMapper;

    @GetMapping
    @Operation(summary = "Get a paginated list of airplanes.", description = "Returns a paginated list of airplanes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airplanes"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<?> getAirplanes(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Airplane> airplanes = airplaneService.findAll(pageRequest);
        Page<AirplaneResponse> airplanesResponse = airplanes.map(airplaneMapper::toResponse);
        return ResponseEntity.ok(new PaginationResponseDto(
                "Airplanes",
                200,
                airplanesResponse
        ));
    }

    @GetMapping("{id}")
    @Operation(summary = "Get an airplane by ID.", description = "Returns details of a specific airplane by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airplane"),
            @ApiResponse(responseCode = "404", description = "Airplane not found")
    })
    public ResponseEntity<?> getAirplaneById(
            @PathVariable Long id
    ) {
        Airplane airplane = airplaneService.findById(id);
        AirplaneResponse airplaneResponse = airplaneMapper.toResponse(airplane);
        return ResponseEntity.ok(new ResponseDto<>(
                "Airplane",
                200,
                airplaneResponse
        ));
    }

    @PostMapping
    @Operation(summary = "Create an airplane.", description = "Creates a new airplane.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created airplane"),
            @ApiResponse(responseCode = "409", description = "Invalid request body")
    })
    public ResponseEntity<?> createAirplane(
            @RequestBody @Valid AirplaneRequest airplane
    ) {
        Airplane savedAirplane = airplaneService.save(airplaneMapper.toAirplane(airplane));
        return ResponseEntity.status(201).body(new ResponseDto<>(
                "Airplane created",
                201,
                airplaneMapper.toResponse(savedAirplane)
        ));
    }

    @PutMapping("{id}")
    @Operation(summary = "Update an airplane.", description = "Updates already created airplane.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created airplane"),
            @ApiResponse(responseCode = "409", description = "Invalid request body")
    })
    public ResponseEntity<?> updateAirplane(
            @PathVariable Long id,
            @RequestBody @Valid AirplaneRequest airplaneReq
    ) {
        Airplane existingAirplane = airplaneService.findById(id);
        existingAirplane.update(airplaneReq);
        Airplane savedAirplane = airplaneService.save(existingAirplane);
        return ResponseEntity.ok(new ResponseDto<>(
                "Airplane updated",
                200,
                airplaneMapper.toResponse(savedAirplane)
        ));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete an airplane.", description = "Deletes an airplane by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted airplane"),
            @ApiResponse(responseCode = "404", description = "Airplane not found"),
            @ApiResponse(responseCode = "400", description = "Airplane already deleted")
    })
    public ResponseEntity<?> deleteAirplane(
            @PathVariable Long id
    ) {
        Airplane airplane = airplaneService.findById(id);
        airplaneService.delete(airplane);
        return ResponseEntity.ok(
                new ResponseDto<>(
                        "Airplane deleted",
                        200,
                        airplaneMapper.toResponse(airplane)
                )
        );
    }

    @PutMapping("{id}/restore")
    @Operation(summary = "Restore an airplane.", description = "Restores a deleted airplane by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully restored airplane"),
            @ApiResponse(responseCode = "400", description = "Airplane already restored"),
            @ApiResponse(responseCode = "404", description = "Airplane not found")
    })
    public ResponseEntity<?> restoreAirplane(
            @PathVariable Long id
    ) {
        Airplane airplane = airplaneService.findByIdWithDeleted(id);
        airplaneService.restore(airplane);
        return ResponseEntity.ok(new ResponseDto<>(
                "Airplane restored",
                200,
                airplaneMapper.toResponse(airplane)
        ));
    }
}
