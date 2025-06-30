package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.TemplateEngine;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.ReservationStatus;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.repository.ReservationRepository;
import pl.sgorski.AirLink.service.auth.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTests {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserService userService;

    @Mock
    private MailService mailService;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private Flight flight;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setFlight(flight);
        reservation.setNumberOfSeats(2);
    }

    @Test
    void shouldSaveReservation() {
        when(flight.isAvailableToBook(anyInt())).thenReturn(true);

        reservationService.save(reservation);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldNotSaveReservationIfFlightHasNoAvailableSeats() {
        when(flight.isAvailableToBook(anyInt())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> reservationService.save(reservation));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldCreateReservation() {
        User user = new User();
        user.setEmail("test@email.com");
        when(flight.isAvailableToBook(anyInt())).thenReturn(true);
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(templateEngine.process(anyString(), any())).thenReturn("Email content");

        Reservation result = reservationService.create(reservation, "test@email.com");

        assertNotNull(result);
        verify(mailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldNotCreateReservation_UserNotFound() {
        when(userService.findByEmail(anyString())).thenThrow(new NoSuchElementException("User not found"));

        assertThrows(NoSuchElementException.class, () -> reservationService.create(reservation, "test@email.com"));

        verify(mailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldNotCreateReservation_NotAvailableToToBook() {
        when(userService.findByEmail(anyString())).thenReturn(new User());
        when(flight.isAvailableToBook(anyInt())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> reservationService.create(reservation, "test@email.com"));

        verify(mailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldFindReservationById() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        reservationService.findById(1L);

        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowIfReservationByIdNotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.findById(1L));

        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnAllReservations() {
        List<Reservation> reservations = List.of(reservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> result = reservationService.findAll();

        assertFalse(result.isEmpty());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void shouldCountAllReservations() {
        when(reservationRepository.count()).thenReturn(5L);

        long count = reservationService.count();

        assertEquals(5L, count);
        verify(reservationRepository, times(1)).count();
    }

    @Test
    void shouldSoftDeleteById() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        reservationService.deleteById(1L);

        assertNotNull(reservation.getDeletedAt());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowWhileSoftDeletingByIdIfNotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.deleteById(1L));

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldRestoreReservationById() {
        reservation.setStatus(ReservationStatus.DELETED);
        when(reservationRepository.findDeletedById(anyLong())).thenReturn(Optional.of(reservation));

        reservationService.restoreById(1L);

        assertNull(reservation.getDeletedAt());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(reservationRepository, times(1)).findDeletedById(1L);
    }

    @Test
    void shouldThrowWhileRestoringByIdIfNotFound() {
        when(reservationRepository.findDeletedById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.restoreById(1L));

        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(reservationRepository, times(1)).findDeletedById(1L);
    }

    @Test
    void shouldUpdateReservationStatus() {
        UpdateReservationRequest updateRequest = new UpdateReservationRequest();
        updateRequest.setStatus("CONFIRMED");
        reservation.setStatus(ReservationStatus.PENDING);
        when(flight.isAvailableToBook(anyInt())).thenReturn(true);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation saved = reservationService.updateReservationById(1L, updateRequest);

        assertEquals(ReservationStatus.CONFIRMED, saved.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldThrowWhenWrongStatusPassed() {
        UpdateReservationRequest updateRequest = new UpdateReservationRequest();
        updateRequest.setStatus("NOTEXISTS");
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        assertThrows(IllegalArgumentException.class, () -> reservationService.updateReservationById(1L, updateRequest));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldThrowWhenNullStatusPassed() {
        UpdateReservationRequest updateRequest = new UpdateReservationRequest();
        updateRequest.setStatus(null);
        reservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        assertThrows(NullPointerException.class, () -> reservationService.updateReservationById(1L, updateRequest));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldReturnAllReservationsForUser() {
        User user = new User();
        Role role = new Role();
        role.setName("USER");
        user.setRole(role);
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Page<Reservation> page = new PageImpl<>(List.of(reservation));
        when(reservationRepository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(userService.findByEmail(anyString())).thenReturn(user);

        Page<Reservation> reservations = reservationService.findAll(PageRequest.of(0, 10));

        assertFalse(reservations.getContent().isEmpty());
        verify(reservationRepository, times(1)).findAllByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldReturnAllReservationsForAdmin() {
        User user = new User();
        Role role = new Role();
        role.setName("ADMIN");
        user.setRole(role);
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Page<Reservation> page = new PageImpl<>(List.of(reservation));
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userService.findByEmail(anyString())).thenReturn(user);

        Page<Reservation> reservations = reservationService.findAll(PageRequest.of(0, 10));

        assertFalse(reservations.getContent().isEmpty());
        verify(reservationRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnTrueIfIsOwner() {
        Role role = new Role();
        role.setName("USER");
        User user = new User();
        user.setId(1L);
        user.setRole(role);
        when(userService.findByEmail(anyString())).thenReturn(user);
        reservation.setUser(user);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        boolean result = reservationService.haveAccessByEmail(1L, "test@email.com");

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueIfIsAdmin() {
        Role role = new Role();
        role.setName("ADMIN");
        User admin = new User();
        admin.setRole(role);
        when(userService.findByEmail(anyString())).thenReturn(admin);
        reservation.setUser(new User());
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        boolean result = reservationService.haveAccessByEmail(1L, "test@email.com");

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseIfDoNotHaveAccess() {
        Role role = new Role();
        role.setName("USER");
        User user = new User();
        user.setRole(role);
        user.setId(2L);
        when(userService.findByEmail(anyString())).thenReturn(user);
        reservation.setUser(new User());
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        boolean result = reservationService.haveAccessByEmail(1L, "test@email.com");

        assertFalse(result);
    }
}
