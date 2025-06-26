package pl.sgorski.AirLink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AirplaneResponse {
    @Schema(description = "Unique identifier of the airplane", example = "1")
    private Long id;
    @Schema(description = "Name of the airplane", example = "Boeing 737")
    private String name;
    @Schema(description = "Code of the airplane", example = "B737")
    private String code;
    @Schema(description = "Unique serial number of the airplane", example = "SN-123456")
    private String serialNumber;
    @Schema(description = "Total seating capacity of the airplane", example = "180")
    private int capacity;
    @Schema(description = "Flight details for the outgoing flight", implementation = FlightResponse.class)
    private FlightResponse incomingFlight;
}
