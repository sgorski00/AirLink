package pl.sgorski.AirLink.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.ReservationResponse;
import pl.sgorski.AirLink.dto.generic.PaginationResponseDto;
import pl.sgorski.AirLink.dto.generic.ResponseDto;
import pl.sgorski.AirLink.dto.NewReservationRequest;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.mapper.ReservationHistoryMapper;
import pl.sgorski.AirLink.mapper.ReservationMapper;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.service.ReservationHistoryService;
import pl.sgorski.AirLink.service.ReservationService;

import java.security.Principal;

@Log4j2
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Endpoints for managing reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationHistoryService reservationHistoryService;
    private final ReservationMapper mapper;
    private final ReservationHistoryMapper historyMapper;

    @GetMapping
    @Operation(summary = "Get all reservations", description = "Retrieve a paginated list of reservations. Content depeneds on user role. " +
            "Admin can see all reservations, while user can only see their own reservations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations found", content = @Content(schema = @Schema(implementation = PaginationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Wrong sorting parameters", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    public ResponseEntity<?> getReservations(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Reservation> reservations = reservationService.findAll(pageRequest);
        Page<ReservationResponse> reservationResponses = reservations.map(mapper::toResponse);
        return ResponseEntity.ok(new PaginationResponseDto(
                reservations.isEmpty() ? "There is no any reservation" : "Reservations found",
                200,
                reservationResponses
        ));
    }

    @GetMapping("{id}")
    @Operation(summary = "Get reservation by ID", description = "Retrieve a reservation by its ID. Access is restricted to the user who created the reservation or an admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation found", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> getReservationById(
            @PathVariable Long id,
            Principal principal
    ) {
        if(!reservationService.haveAccessByEmail(id, principal.getName())) throw new AccessDeniedException("You do not have access to this reservation");
        return ResponseEntity.ok(new ResponseDto<>(
            "Reservation found",
            200,
            mapper.toResponse(reservationService.findById(id))
        ));
    }

    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Create a new reservation. The user must be authenticated to create a reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody NewReservationRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(201).body(new ResponseDto<>(
            "Reservation created",
            201,
            mapper.toResponse(reservationService.create(mapper.toReservation(request), principal.getName()))
        ));
    }

    @PutMapping("{id}")
    @Operation(summary = "Update a reservation", description = "Update an existing reservation by its ID. Access is restricted to the user who created the reservation or an admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReservationRequest request,
            Principal principal
    ) {
        if(!reservationService.haveAccessByEmail(id, principal.getName())) throw new AccessDeniedException("You do not have access to this reservation");
        Reservation updated = reservationService.updateReservationById(id, request);
        return ResponseEntity.ok(new ResponseDto<>(
            "Reservation updated",
            200,
            mapper.toResponse(updated)
        ));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a reservation", description = "Delete a reservation by its ID. Access is restricted to the user who created the reservation or an admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation deleted", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> deleteReservation(
            @PathVariable Long id,
            Principal principal
    ) {
        if(!reservationService.haveAccessByEmail(id, principal.getName())) throw new AccessDeniedException("You do not have access to this reservation");
        return ResponseEntity.ok(new ResponseDto<>(
            "Reservation deleted",
            200,
            mapper.toResponse(reservationService.deleteById(id))
        ));
    }

    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore a deleted reservation", description = "Restore a reservation that was previously deleted. Access is restricted to the user who created the reservation or an admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation restored", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> restoreReservation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new ResponseDto<>(
                "Reservation restored",
                200,
                mapper.toResponse(reservationService.restoreById(id))
        ));
    }

    @GetMapping("{id}/history")
    @Operation(summary = "Get reservation history", description = "Retrieve the history of a reservation by its ID. Access is restricted to the user who created the reservation or an admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation history found", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> getReservationHistory(
            @PathVariable Long id,
            Principal principal
    ) {
        if(!reservationService.haveAccessByEmail(id, principal.getName())) throw new AccessDeniedException("You do not have access to this reservation");
        return ResponseEntity.ok(new ResponseDto<>(
                "Reservation history found",
                200,
                reservationHistoryService.getHistoryByReservationId(id).stream()
                        .map(historyMapper::toResponse)
        ));
    }
}
