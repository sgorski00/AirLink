package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.repository.ReservationRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation save(Reservation reservation) {
        if(!reservation.getFlight().hasAvailableSeats(reservation.getNumberOfSeats())){
            throw new IllegalArgumentException("Not enough available seats on the flight.");
        }
        return reservationRepository.save(reservation);
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation deleteById(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                        () -> new NoSuchElementException("Reservation not found or already deleted")
        );
        reservation.setDeletedAt(Timestamp.from(Instant.now()));
        return reservationRepository.save(reservation);
    }


    public Reservation restoreById(Long id) {
        Reservation reservation = reservationRepository.findDeletedById(id).orElseThrow(
                () -> new NoSuchElementException("Reservation not found or not deleted")
        );
        reservation.setDeletedAt(null);
        return reservationRepository.save(reservation);
    }

    public long count() {
        return reservationRepository.count();
    }
}
