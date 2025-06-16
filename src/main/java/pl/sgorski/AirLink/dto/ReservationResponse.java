package pl.sgorski.AirLink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReservationResponse {
    @Schema(example = "1", description = "Unique identifier of the reservation")
    private Long id;
    @Schema(example = "john_doe@yahoo.com", description = "Email of the person who made the reservation")
    private String user;
    private FlightResponse flight;
    @Schema(example = "2", description = "Number of seats reserved")
    private Integer numberOfSeats;
    @Schema(example = "150.00", description = "Total price for the reservation")
    private Double totalPrice;
    @Schema(example = "CONFIRMED", description = "Current status of the reservation")
    private String status;
    @Schema(example = "2023-10-01T12:00:00Z", description = "Timestamp when the reservation was created")
    private String createdAt;
}
