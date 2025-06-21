package pl.sgorski.AirLink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReservationHistoryResponse {
    @Schema(description = "Unique identifier of the reservation history entry")
    private Long id;
    @Schema(description = "Identifier of the reservation associated with this history entry")
    private Long reservationId;
    @Schema(description = "History entry status")
    private String status;
    @Schema(description = "Date and time when the history entry was created")
    private String createdAt;
}
