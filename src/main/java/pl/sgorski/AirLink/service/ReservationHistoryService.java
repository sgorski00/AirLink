package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.ReservationHistory;
import pl.sgorski.AirLink.repository.ReservationHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationHistoryService {

    private final ReservationHistoryRepository reservationHistoryRepository;

    public List<ReservationHistory> getHistoryByReservationId(Long reservationId) {
        return reservationHistoryRepository.findAllByReservationId(reservationId);
    }
}
