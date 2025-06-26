package pl.sgorski.AirLink.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.AirportRequest;
import pl.sgorski.AirLink.dto.AirportResponse;
import pl.sgorski.AirLink.dto.generic.PaginationResponseDto;
import pl.sgorski.AirLink.dto.generic.ResponseDto;
import pl.sgorski.AirLink.mapper.AirportMapper;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.service.AirportService;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
@Tag(name = "Airports", description = "Endpoints for managing airports")
public class AirportController {

    private final AirportService airportService;
    private final AirportMapper airportMapper;

    @GetMapping
    @Operation(summary = "Get a paginated list of airports.", description = "Returns a paginated list of airports. Can be filtered by country ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airports"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<?> getAirports(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "country", required = false) Long countryId
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Airport> airports = airportService.findAll(countryId, pageRequest);
        Page<AirportResponse> airportsResponse = airports.map(airportMapper::toResponse);
        return ResponseEntity.ok(new PaginationResponseDto(
                "Airports",
                200,
                airportsResponse
        ));
    }

    @GetMapping("{id}")
    @Operation(summary = "Get an airport by ID.", description = "Returns details of a specific airport by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airport"),
            @ApiResponse(responseCode = "404", description = "Airport not found")
    })
    public ResponseEntity<?> getAirports(
            @PathVariable Long id
    ) {
        Airport airport = airportService.findById(id);
        AirportResponse airportResponse = airportMapper.toResponse(airport);
        return ResponseEntity.ok(new ResponseDto<>(
                "Airport",
                200,
                airportResponse
        ));
    }

    @PostMapping
    @Operation(summary = "Create a new airport.", description = "Creates a new airport with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Airport created successfully"),
            @ApiResponse(responseCode = "409", description = "Invalid request data")
    })
    public ResponseEntity<?> createAirport(
            @RequestBody AirportRequest airportRequest
    ) {
        Airport airport = airportMapper.toAirport(airportRequest);
        Airport savedAirport = airportService.save(airport);
        return ResponseEntity.status(201).body(new ResponseDto<>(
                "Airport created",
                201,
                airportMapper.toResponse(savedAirport)
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing airport.", description = "Updates the details of an existing airport by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Airport updated successfully"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "409", description = "Invalid request data")
    })
    public ResponseEntity<?> updateAirport(
            @PathVariable Long id,
            @RequestBody AirportRequest airportRequest
    ) {
        Airport existingAirport = airportService.findById(id);
        existingAirport.update(airportMapper.toAirport(airportRequest));
        Airport savedAirport = airportService.save(existingAirport);
        return ResponseEntity.status(200).body(new ResponseDto<>(
                "Airport updated",
                200,
                airportMapper.toResponse(savedAirport)
        ));
    }
}
