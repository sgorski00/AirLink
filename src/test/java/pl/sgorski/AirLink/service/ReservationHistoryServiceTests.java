package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.AirLink.model.Reservation;
import pl.sgorski.AirLink.model.ReservationHistory;
import pl.sgorski.AirLink.repository.ReservationHistoryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationHistoryServiceTests {

    @Mock
    private ReservationHistoryRepository reservationHistoryRepository;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationHistoryService reservationHistoryService;

    @Test
    void shouldReturnHistoryById(){
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationService.returnIfHaveAccess(anyLong(), anyString())).thenReturn(reservation);
        when(reservationHistoryRepository.findAllByReservationId(1L)).thenReturn(List.of());

        List<ReservationHistory> result = reservationHistoryService.getHistoryByReservationId(1L, "test@email.com");

        assertNotNull(result);
    }

}
