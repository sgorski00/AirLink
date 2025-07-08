package pl.sgorski.AirLink.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.generic.PaginationResponseDto;
import pl.sgorski.AirLink.dto.generic.ResponseDto;
import pl.sgorski.AirLink.dto.FlightRequest;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.mapper.FlightMapper;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.service.FlightService;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flights", description = "Endpoints for managing flights")
public class FlightController {

    private final FlightService flightService;
    private final FlightMapper flightMapper;

    @GetMapping
    @Operation(summary = "Get all flights", description = "Retrieve a paginated list of all flights")
    @ApiResponse(responseCode = "200", description = "Flights found", content = @Content(schema = @Schema(implementation = PaginationResponseDto.class)))
    public ResponseEntity<?> getFlights(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "departure") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long airportFrom,
            @RequestParam(required = false) Long airportTo
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Flight> flights = flightService.findAllActivePaginated(pageRequest, airportFrom, airportTo);
        if(flights.isEmpty()) return ResponseEntity.ok(new PaginationResponseDto("Flights", 200, Page.empty()));
        Page<FlightResponse> flightsResponse = flights.map(flightMapper::toResponse);
        return ResponseEntity.ok(new PaginationResponseDto("Flights", 200, flightsResponse));
    }

    @GetMapping("{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieve a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight found", content = @Content(schema = @Schema(implementation = FlightResponse.class))),
            @ApiResponse(responseCode = "404", description = "Flight not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> getFlightById(
            @PathVariable Long id
    ) {
        FlightResponse flightResponse = flightMapper.toResponse(flightService.findById(id));
        return ResponseEntity.ok(new ResponseDto<>("Flight found", 200, flightResponse));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete flight by ID", description = "Soft delete a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight deleted", content = @Content(schema = @Schema(implementation = FlightResponse.class))),
            @ApiResponse(responseCode = "404", description = "Flight not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> deleteFlight(
            @PathVariable Long id
    ) {
        FlightResponse deletedFLight = flightMapper.toResponse(flightService.deleteById(id));
        return ResponseEntity.ok(new ResponseDto<>("Flight deleted", 200, deletedFLight));
    }

    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore flight by ID", description = "Restore a soft-deleted flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight restored", content = @Content(schema = @Schema(implementation = FlightResponse.class))),
            @ApiResponse(responseCode = "404", description = "Flight not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> restoreFlight(
            @PathVariable Long id
    ) {
        FlightResponse restoredFlight = flightMapper.toResponse(flightService.restoreById(id));
        return ResponseEntity.ok(new ResponseDto<>("Flight restored", 200, restoredFlight));
    }

    @PutMapping("{id}")
    @Operation(summary = "Update flight by ID", description = "Update a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight updated", content = @Content(schema = @Schema(implementation = FlightResponse.class))),
            @ApiResponse(responseCode = "404", description = "Flight not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequest flightRequest
    ) {
        Flight existingFlight = flightService.findById(id);
        flightMapper.updateFlight(flightRequest, existingFlight);
        FlightResponse updatedFlight = flightMapper.toResponse(flightService.save(existingFlight));
        return ResponseEntity.ok(new ResponseDto<>("Flight updated", 200, updatedFlight));
    }

    @PostMapping
    @Operation(summary = "Create a new flight", description = "Create a new flight with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created successfully", content = @Content(schema = @Schema(implementation = FlightResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> createFlight(
            @Valid @RequestBody FlightRequest flightRequest
    ) {
        Flight flight = flightMapper.toFlight(flightRequest);
        FlightResponse createdFlight = flightMapper.toResponse(flightService.save(flight));
        return ResponseEntity.status(201).body(new ResponseDto<>("Flight created", 201, createdFlight));
    }
}
