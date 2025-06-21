package pl.sgorski.AirLink.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.sgorski.AirLink.dto.ReservationHistoryResponse;
import pl.sgorski.AirLink.model.ReservationHistory;

@Mapper(componentModel = "spring")
public interface ReservationHistoryMapper {

    @Mapping(target = "reservationId", source = "reservation.id")
    ReservationHistoryResponse toResponse(ReservationHistory history);
}
