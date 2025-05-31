package pl.sgorski.AirLink.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightRequest {

    @NotNull(message = "Airplane ID cannot be null")
    private Long airplaneId;

    @FutureOrPresent(message = "Departure time must be in the future or present")
    private LocalDateTime departure;

    @FutureOrPresent(message = "Arrival time must be in the future or present")
    private LocalDateTime arrival;

    @NotNull(message = "Departure airport ID cannot be null")
    private Long fromAirportId;

    @NotNull(message = "Destination airport ID cannot be null")
    private Long toAirportId;

    @Positive(message = "Price must be positive")
    private Double price;
}
