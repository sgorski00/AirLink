package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import pl.sgorski.AirLink.dto.UpdateReservationRequest;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.ReservationStatus;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.repository.ReservationRepository;
import pl.sgorski.AirLink.service.auth.UserService;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final MailService mailService;
    private final TemplateEngine templateEngine;

    public Reservation save(Reservation reservation) {
        if(!reservation.getFlight().isAvailableToBook(reservation.getNumberOfSeats())){
            throw new IllegalArgumentException("This flight is no longer available to book.");
        }
        return reservationRepository.save(reservation);
    }

    public Reservation create(Reservation reservation, String userEmail) {
        log.debug("Creating reservation for user: {}", userEmail);
        log.debug("Reservation details: {}", reservation);
        User user = userService.findByEmail(userEmail);
        reservation.setUser(user);
        Reservation savedReservation = save(reservation);
        mailService.sendEmail(
                savedReservation.getUser().getEmail(),
                "AirLink - Reservation no. " + savedReservation.getId(),
                templateEngine.process("reservation-email", savedReservation.toEmailContext())
        );
        log.info("Reservation email sent");
        return savedReservation;
    }

    public Reservation findById(Long id, String requesterEmail) {
        return returnIfHaveAccess(id, requesterEmail);
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Page<Reservation> findAll(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        return user.getRole().isAdmin()
                ? reservationRepository.findAll(pageable)
                : reservationRepository.findAllByUserId(user.getId(), pageable);
    }

    public Reservation deleteById(Long id, String requesterEmail) {
        Reservation reservation = returnIfHaveAccess(id, requesterEmail);
        reservation.setStatus(ReservationStatus.DELETED);
        return reservationRepository.save(reservation);
    }


    public Reservation restoreById(Long id) {
        Reservation reservation = reservationRepository.findDeletedById(id).orElseThrow(
                () -> new NoSuchElementException("Reservation not found or not deleted")
        );
        reservation.restore();
        return reservationRepository.save(reservation);
    }

    public long count() {
        return reservationRepository.count();
    }

    public Reservation updateReservationById(Long id, UpdateReservationRequest request, String requesterEmail) {
        Reservation reservation = findById(id, requesterEmail);
        try{
            reservation.setStatus(ReservationStatus.valueOf(request.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status provided: " + request.getStatus());
        }
        return save(reservation);
    }

    public Reservation returnIfHaveAccess(Long reservationId, String requesterEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));
        User user = userService.findByEmail(requesterEmail);
        if(user.haveAccess(reservation)) {
            return reservation;
        } else {
            throw new AccessDeniedException("You do not have access to this reservation");
        }
    }
}
