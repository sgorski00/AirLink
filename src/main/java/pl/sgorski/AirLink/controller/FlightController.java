package pl.sgorski.AirLink.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.ApiResponse;
import pl.sgorski.AirLink.dto.FlightRequest;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.mapper.FlightMapper;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.service.FlightService;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final FlightMapper flightMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightResponse>>> getFlights() {
        List<FlightResponse> flights = flightService.findAll().stream()
                .map(flightMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new ApiResponse<>("Flights", 200, flights));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<FlightResponse>> deleteFlight(
            @PathVariable Long id
    ) {
        FlightResponse deletedFLight = flightMapper.toResponse(flightService.deleteFlightById(id));
        return ResponseEntity.ok(new ApiResponse<>("Flight deleted", 200, deletedFLight));
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<FlightResponse>> restoreFlight(
            @PathVariable Long id
    ) {
        FlightResponse restoredFlight = flightMapper.toResponse(flightService.restoreById(id));
        return ResponseEntity.ok(new ApiResponse<>("Flight restored", 200, restoredFlight));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<FlightResponse>> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequest flightRequest
    ) {
        Flight existingFlight = flightService.findById(id);
        flightMapper.updateFlight(flightRequest, existingFlight);
        FlightResponse updatedFlight = flightMapper.toResponse(flightService.save(existingFlight));
        return ResponseEntity.ok(new ApiResponse<>("Flight updated", 200, updatedFlight));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FlightResponse>> createFlight(
            @Valid @RequestBody FlightRequest flightRequest
    ) {
        Flight flight = flightMapper.toFlight(flightRequest);
        FlightResponse createdFlight = flightMapper.toResponse(flightService.save(flight));
        return ResponseEntity.status(201).body(new ApiResponse<>("Flight created", 201, createdFlight));
    }
}
