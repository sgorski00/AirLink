package pl.sgorski.AirLink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AirplaneRequest {

    @NotBlank(message = "Airplane name cannot be blank")
    private String name;
    @NotBlank(message = "Airplane code cannot be blank")
    private String code;
    @NotBlank(message = "Airplane serial number cannot be blank")
    private String serialNumber;
    @Positive(message = "Airplane capacity must be a positive number")
    private int capacity;
}
