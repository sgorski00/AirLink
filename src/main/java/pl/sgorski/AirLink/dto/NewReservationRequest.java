package pl.sgorski.AirLink.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NewReservationRequest {
    @NotNull(message = "Flight must be specified")
    private Long flightId;
    @Positive(message = "Number of seats must be a positive number")
    @NotNull(message = "Number of seats must be specified")
    private Integer numberOfSeats;
}
