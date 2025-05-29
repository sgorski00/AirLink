package pl.sgorski.AirLink.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.AirLink.dto.ApiResponse;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.service.FlightService;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightResponse>>> getFlights() {
        return ResponseEntity.ok(new ApiResponse<>("Flights", 200, flightService.findAllResponses()));
    }
}
