package pl.sgorski.AirLink.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.ApiResponse;
import pl.sgorski.AirLink.dto.NewReservationRequest;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.mapper.ReservationMapper;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper mapper;

    @GetMapping
    public ResponseEntity<?> getReservations() {
        //TODO: Add reservation only for logged user or leave as it is for admin
        //TODO: Add pagination and filtering
        return ResponseEntity.ok(new ApiResponse<>(
            "Reservations found",
            200,
            reservationService.findAll().stream()
                .map(mapper::toResponse)
                .toList()
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getReservationById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new ApiResponse<>(
            "Reservation found",
            200,
            mapper.toResponse(reservationService.findById(id))
        ));
    }

    @PostMapping
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody NewReservationRequest request
    ) {
        return ResponseEntity.status(201).body(new ApiResponse<>(
            "Reservation created",
            201,
            mapper.toResponse(reservationService.save(mapper.toReservation(request)))
        ));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReservationRequest request
    ) {
        Reservation updated = reservationService.updateReservationById(id, request);
        return ResponseEntity.ok(new ApiResponse<>(
            "Reservation updated",
            200,
            mapper.toResponse(updated)
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteReservation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new ApiResponse<>(
            "Reservation deleted",
            200,
            mapper.toResponse(reservationService.deleteById(id))
        ));
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restoreReservation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new ApiResponse<>(
                "Reservation restored",
                200,
                mapper.toResponse(reservationService.restoreById(id))
        ));
    }
}
