package pl.sgorski.AirLink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReservationRequest {
    @NotNull(message = "New status must be specified")
    private String status;
}
