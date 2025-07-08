package pl.sgorski.AirLink.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.AirLink.dto.NewReservationRequest;
import pl.sgorski.AirLink.dto.ReservationResponse;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.mapper.ReservationHistoryMapper;
import pl.sgorski.AirLink.mapper.ReservationMapper;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.ReservationStatus;
import pl.sgorski.AirLink.service.ReservationHistoryService;
import pl.sgorski.AirLink.service.ReservationService;
import pl.sgorski.AirLink.service.auth.JwtService;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationMapper reservationMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private ReservationHistoryMapper historyMapper;

    @MockitoBean
    private ReservationHistoryService reservationHistoryService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnAllReservations() throws Exception {
        Page<Reservation> reservations = new PageImpl<>(List.of(new Reservation()));
        when(reservationService.findAll(any(PageRequest.class))).thenReturn(reservations);

        mockMvc.perform(get("/api/reservations")
                        .principal(() -> "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Reservations found"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(reservationService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyListForLoggedUser() throws Exception {
        when(reservationService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/reservations")
                        .principal(() -> "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("There is no any reservation"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(reservationService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnAllReservationsForLoggedUser() throws Exception {
        Page<Reservation> reservations = new PageImpl<>(List.of(new Reservation()));
        when(reservationService.findAll(any(Pageable.class))).thenReturn(reservations);

        mockMvc.perform(get("/api/reservations")
                        .principal(() -> "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Reservations found"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(reservationService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnReservationById() throws Exception {
        ReservationResponse response = new ReservationResponse();
        response.setId(1L);
        response.setStatus("CONFIRMED");
        response.setUser("testUser");

        when(reservationService.findById(anyLong(), anyString())).thenReturn(new Reservation());
        when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(response);

        mockMvc.perform(get("/api/reservations/{id}", 1L)
                        .principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Reservation found"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.user").value("testUser"));
    }

    @Test
    void shouldReturnDetailProblemIfReservationNotFound() throws Exception {
        when(reservationService.findById(anyLong(), anyString())).thenThrow(
                new NoSuchElementException("Reservation not found")
        );

        mockMvc.perform(get("/api/reservations/{id}", 1L)
                        .principal(() -> "testUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Reservation not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnDetailProblemIfUserNotAllowed() throws Exception {
        when(reservationService.findById(anyLong(), anyString())).thenThrow(new AccessDeniedException("You do not have access to this reservation"));


        mockMvc.perform(get("/api/reservations/{id}", 1L)
                        .principal(() -> "testUser"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You do not have access to this reservation"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldCreateNewReservation() throws Exception {
        NewReservationRequest request = new NewReservationRequest();
        request.setFlightId(1L);
        request.setNumberOfSeats(2);

        when(reservationMapper.toResponse(nullable(Reservation.class))).thenReturn(new ReservationResponse());

        mockMvc.perform(post("/api/reservations")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detail").value("Reservation created"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(reservationService, times(1)).create(nullable(Reservation.class), anyString());
    }

    @Test
    void shouldNotCreateNewReservationIfFlightNotAvailable() throws Exception {
        NewReservationRequest request = new NewReservationRequest();
        request.setFlightId(1L);
        request.setNumberOfSeats(2);

        when(reservationService.create(nullable(Reservation.class), anyString())).thenThrow(
                new IllegalArgumentException("This flight is no longer available to book.")
        );

        mockMvc.perform(post("/api/reservations")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("This flight is no longer available to book."))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, times(1)).create(nullable(Reservation.class), anyString());
    }

    @Test
    void shouldNotCreateNewReservationIfSeatsAreNotPositive() throws Exception {
        NewReservationRequest request = new NewReservationRequest();
        request.setFlightId(1L);
        request.setNumberOfSeats(-2);

        mockMvc.perform(post("/api/reservations")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).create(nullable(Reservation.class), anyString());
    }

    @Test
    void shouldNotCreateNewReservationIfUserIsNull() throws Exception {
        NewReservationRequest request = new NewReservationRequest();
        request.setFlightId(1L);
        request.setNumberOfSeats(-2);

        mockMvc.perform(post("/api/reservations")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).create(nullable(Reservation.class), nullable(String.class));
    }

    @Test
    void shouldNotCreateNewReservationIfFlightIsNull() throws Exception {
        NewReservationRequest request = new NewReservationRequest();
        request.setNumberOfSeats(-2);

        mockMvc.perform(post("/api/reservations")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).create(nullable(Reservation.class), anyString());
    }

    @Test
    void shouldNotCreateNewReservationIfNumberOfSeatsIsNull() throws Exception {
        NewReservationRequest request = new NewReservationRequest();
        request.setFlightId(1L);

        mockMvc.perform(post("/api/reservations")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).create(nullable(Reservation.class), anyString());
    }

    @Test
    void shouldUpdateReservationStatus() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PENDING);

        when(reservationService.findById(anyLong(), anyString())).thenReturn(reservation);
        when(reservationService.updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString())).thenReturn(reservation);
        when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(new ReservationResponse());

        mockMvc.perform(put("/api/reservations/{id}", 1L)
                        .contentType("application/json")
                        .content("{\"status\":\"CONFIRMED\"}")
                        .principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Reservation updated"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(reservationService, times(1)).updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString());
    }

    @Test
    void shouldNotUpdateReservationStatusIfWrongName() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PENDING);

        when(reservationService.findById(anyLong(), anyString())).thenReturn(reservation);
        when(reservationService.updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString())).thenThrow(new IllegalArgumentException("Invalid status provided: NOTEXISTS"));
        when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(new ReservationResponse());

        mockMvc.perform(put("/api/reservations/{id}", 1L)
                        .contentType("application/json")
                        .content("{\"status\":\"NOTEXISTS\"}")
                        .principal(() -> "testUser"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid status provided: NOTEXISTS"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, times(1)).updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString());
    }

    @Test
    void shouldNotUpdateReservationStatusIfBodyNotPassed() throws Exception {
        mockMvc.perform(put("/api/reservations/{id}", 1L)
                        .principal(() -> "testUser"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString());
    }

    @Test
    void shouldNotUpdateReservationStatusIfReservationNotFound() throws Exception {
        when(reservationService.updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString()))
                .thenThrow(new NoSuchElementException("Reservation not found"));

        mockMvc.perform(put("/api/reservations/{id}", 1L)
                        .contentType("application/json")
                        .content("{\"status\":\"CONFIRMED\"}")
                        .principal(() -> "testUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Reservation not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, times(1)).updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString());
    }

    @Test
    void shouldNotUpdateReservationStatusIfUserNotAllowed() throws Exception {
        when(reservationService.updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString())).thenThrow(new AccessDeniedException("You do not have access to this reservation"));

        mockMvc.perform(put("/api/reservations/{id}", 1L)
                        .contentType("application/json")
                        .content("{\"status\":\"CONFIRMED\"}")
                        .principal(() -> "testUser"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You do not have access to this reservation"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, times(1)).updateReservationById(anyLong(), any(UpdateReservationRequest.class), anyString());
    }


    @Test
    void shouldDeleteReservation() throws Exception {
        when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(new ReservationResponse());
        when(reservationService.deleteById(anyLong(), anyString())).thenReturn(new Reservation());

        mockMvc.perform(delete("/api/reservations/{id}", 1L)
                        .principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Reservation deleted"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(reservationService, times(1)).deleteById(anyLong(), anyString());
    }

    @Test
    void shouldNotDeleteReservationIfNotFound() throws Exception {
        when(reservationService.deleteById(anyLong(), anyString())).thenThrow(new NoSuchElementException("Reservation not found"));

        mockMvc.perform(delete("/api/reservations/{id}", 1L)
                        .principal(() -> "testUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Reservation not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, times(1)).deleteById(anyLong(), anyString());
    }

    @Test
    void shouldNotDeleteReservationIfIdNotPassed() throws Exception {
        mockMvc.perform(delete("/api/reservations/")
                        .principal(() -> "testUser"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).deleteById(anyLong(), anyString());
    }

    @Test
    void shouldNotDeleteReservationIfIdIsNull() throws Exception {
        mockMvc.perform(delete("/api/reservations/{id}", (Long) null)
                        .principal(() -> "testUser"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).deleteById(anyLong(), anyString());
    }

    @Test
    void shouldRestoreReservation() throws Exception {
        Reservation restoredReservation = new Reservation();
        restoredReservation.setId(1L);
        restoredReservation.setStatus(ReservationStatus.PENDING);

        when(reservationService.restoreById(anyLong())).thenReturn(restoredReservation);
        when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(new ReservationResponse());

        mockMvc.perform(put("/api/reservations/restore/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Reservation restored"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(reservationService, times(1)).restoreById(anyLong());
    }

    @Test
    void shouldNotRestoreReservationIfNotFound() throws Exception {
        when(reservationService.restoreById(anyLong())).thenThrow(new NoSuchElementException("Reservation not found"));

        mockMvc.perform(put("/api/reservations/restore/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Reservation not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, times(1)).restoreById(anyLong());
    }

    @Test
    void shouldNotRestoreReservationIfIdNotPassed() throws Exception {
        mockMvc.perform(put("/api/reservations/restore/"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).restoreById(anyLong());
    }

    @Test
    void shouldNotRestoreReservationIfIdIsNull() throws Exception {
        mockMvc.perform(put("/api/reservations/restore/{id}", (Long) null))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reservationService, never()).restoreById(anyLong());
    }
}
