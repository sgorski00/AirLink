package pl.sgorski.AirLink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class FlightResponse {
    @Schema(description = "Flight ID", example = "1")
    private Long id;
    @Schema(description = "Flight number", example = "FL1234")
    private String from;
    @Schema(description = "Flight destination", example = "New York")
    private String to;
    @Schema(description = "Flight departure time", example = "2023-10-01T14:30:00")
    private LocalDateTime departure;
    @Schema(description = "Flight arrival time", example = "2023-10-01T16:45:00")
    private LocalDateTime arrival;
    @Schema(description = "Flight price", example = "199.99")
    private Double price;
    @Schema(description = "Airplane name", example = "Boeing 737")
    private String airplaneName;
    @Schema(description = "Flight created at", example = "2023-10-01T12:00:00")
    private Timestamp createdAt;
}
