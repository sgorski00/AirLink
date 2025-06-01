package pl.sgorski.AirLink.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.ApiResponse;
import pl.sgorski.AirLink.dto.ReservationRequest;
import pl.sgorski.AirLink.dto.ReservationResponse;
import pl.sgorski.AirLink.mapper.ReservationMapper;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper mapper;

    @GetMapping
    public ApiResponse<List<ReservationResponse>> getReservations() {
        return new ApiResponse<>(
            "Reservations found",
            200,
            reservationService.findAll().stream()
                .map(mapper::toResponse)
                .toList()
        );
    }

    @GetMapping("{id}")
    public ApiResponse<ReservationResponse> getReservationById(
            @PathVariable Long id
    ) {
        return new ApiResponse<>(
            "Reservation found",
            200,
            mapper.toResponse(reservationService.findById(id))
        );
    }

    @PostMapping
    public ApiResponse<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request
    ) {
        return new ApiResponse<>(
            "Reservation created",
            201,
            mapper.toResponse(reservationService.save(mapper.toReservation(request)))
        );
    }

    @PutMapping("{id}")
    public ApiResponse<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationRequest request
    ) {
        Reservation reservation = reservationService.findById(id);
        mapper.updateReservation(request, reservation);
        return new ApiResponse<>(
            "Reservation updated",
            200,
            mapper.toResponse(reservationService.save(reservation))
        );
    }

    @DeleteMapping("{id}")
    public ApiResponse<ReservationResponse> deleteReservation(
            @PathVariable Long id
    ) {
        return new ApiResponse<>(
            "Reservation deleted",
            200,
            mapper.toResponse(reservationService.deleteById(id))
        );
    }

    @PutMapping("/restore/{id}")
    public ApiResponse<ReservationResponse> restoreReservation(
            @PathVariable Long id
    ) {
        return new ApiResponse<>(
                "Reservation restored",
                200,
                mapper.toResponse(reservationService.restoreById(id))
        );
    }
}
