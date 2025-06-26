package pl.sgorski.AirLink.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AirportRequest {

    @NotNull
    private Long cityId;

    @NotNull
    @Size(min = 4, max = 4, message = "ICAO code must be exactly 4 characters long")
    private String icao;
}
