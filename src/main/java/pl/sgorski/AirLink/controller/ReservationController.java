package pl.sgorski.AirLink.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.generic.ResponseDto;
import pl.sgorski.AirLink.dto.NewReservationRequest;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.mapper.ReservationMapper;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.service.ReservationService;
import pl.sgorski.AirLink.service.auth.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final ReservationMapper mapper;

    @GetMapping
    public ResponseEntity<?> getReservations(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Principal principal
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        User user = userService.findByEmail(principal.getName());
        Page<Reservation> reservations = user.getRole().isAdmin() ?
                reservationService.findAll(pageRequest) :
                reservationService.findAllByUserId(user.getId(), pageRequest);
        return ResponseEntity.ok(new ResponseDto<>(
            reservations.isEmpty() ? "There is no any reservation" :"Reservations found",
            200,
            reservations.stream()
                .map(mapper::toResponse)
                .toList()
        ));
    }

    @GetMapping("{id}")
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
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody NewReservationRequest request
    ) {
        return ResponseEntity.status(201).body(new ResponseDto<>(
            "Reservation created",
            201,
            mapper.toResponse(reservationService.save(mapper.toReservation(request)))
        ));
    }

    @PutMapping("{id}")
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
    public ResponseEntity<?> restoreReservation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new ResponseDto<>(
                "Reservation restored",
                200,
                mapper.toResponse(reservationService.restoreById(id))
        ));
    }
}
