package pl.sgorski.AirLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.AirLink.model.ReservationHistory;

import java.util.List;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {
    List<ReservationHistory> findAllByReservationId(Long reservationId);
}
