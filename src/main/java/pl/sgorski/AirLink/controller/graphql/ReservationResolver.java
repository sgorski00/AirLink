package pl.sgorski.AirLink.controller.graphql;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sgorski.AirLink.dto.NewReservationRequest;
import pl.sgorski.AirLink.dto.ReservationHistoryResponse;
import pl.sgorski.AirLink.dto.ReservationResponse;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.dto.generic.PageInput;
import pl.sgorski.AirLink.dto.generic.SortInput;
import pl.sgorski.AirLink.mapper.ReservationHistoryMapper;
import pl.sgorski.AirLink.mapper.ReservationMapper;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.service.ReservationHistoryService;
import pl.sgorski.AirLink.service.ReservationService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservationResolver {

    private final ReservationService reservationService;
    private final ReservationHistoryService reservationHistoryService;
    private final ReservationMapper reservationMapper;
    private final ReservationHistoryMapper reservationHistoryMapper;

    @QueryMapping("reservations")
    @PreAuthorize("isAuthenticated()")
    public Page<ReservationResponse> getReservations(
            @Argument PageInput pageInput,
            @Argument SortInput sortInput
    ) {
        Sort sort = sortInput.toSort();
        PageRequest pageRequest = pageInput.toPageRequest(sort);
        Page<Reservation> reservationPage = reservationService.findAll(pageRequest);
        return reservationPage.map(reservationMapper::toResponse);
    }

    @QueryMapping("reservation")
    @PreAuthorize("isAuthenticated()")
    public ReservationResponse getReservationById(@Argument Long id, Principal principal) {
        Reservation reservation = reservationService.findById(id, principal.getName());
        return reservationMapper.toResponse(reservation);
    }

    @MutationMapping("createReservation")
    @PreAuthorize("isAuthenticated()")
    public ReservationResponse createReservation(
            @Argument @Valid NewReservationRequest request,
            Principal principal
    ) {
        Reservation newReservation = reservationMapper.toReservation(request);
        Reservation reservation = reservationService.create(newReservation, principal.getName());
        return reservationMapper.toResponse(reservation);
    }

    @MutationMapping("updateReservation")
    @PreAuthorize("isAuthenticated()")
    public ReservationResponse updateReservation(
            @Argument Long id,
            @Argument @Valid UpdateReservationRequest request,
            Principal principal
    ) {
        Reservation updatedReservation = reservationService.updateReservationById(id, request, principal.getName());
        return reservationMapper.toResponse(updatedReservation);
    }

    @MutationMapping("deleteReservation")
    @PreAuthorize("isAuthenticated()")
    public ReservationResponse deleteReservation(@Argument Long id, Principal principal) {
        Reservation deletedReservation = reservationService.deleteById(id, principal.getName());
        return reservationMapper.toResponse(deletedReservation);
    }

    @MutationMapping("restoreReservation")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ReservationResponse restoreReservation(@Argument Long id) {
        Reservation restoredReservation = reservationService.restoreById(id);
        return reservationMapper.toResponse(restoredReservation);
    }

    @QueryMapping("reservationHistory")
    @PreAuthorize("isAuthenticated()")
    public List<ReservationHistoryResponse> getReservationHistory(@Argument Long id, Principal principal) {
        return reservationHistoryService.getHistoryByReservationId(id, principal.getName()).stream()
                .map(reservationHistoryMapper::toResponse)
                .toList();
    }
}
