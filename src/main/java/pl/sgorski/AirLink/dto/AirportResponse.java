package pl.sgorski.AirLink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AirportResponse {

    @Schema(description = "Airport ID", example = "1")
    private Long id;
    @Schema(description = "Airport ICAO code", example = "KJFK")
    private String icao;
    @Schema(description = "Airport location - city", example = "New York")
    private String city;
    @Schema(description = "Airport location - country", example = "United States of America")
    private String country;
}
