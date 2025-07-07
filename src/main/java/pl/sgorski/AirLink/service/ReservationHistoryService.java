package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.ReservationHistory;
import pl.sgorski.AirLink.repository.ReservationHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationHistoryService {

    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReservationService reservationService;

    public List<ReservationHistory> getHistoryByReservationId(Long reservationId, String requesterEmail) {
        Reservation res = reservationService.returnIfHaveAccess(reservationId, requesterEmail);
        return reservationHistoryRepository.findAllByReservationId(res.getId());
    }
}
