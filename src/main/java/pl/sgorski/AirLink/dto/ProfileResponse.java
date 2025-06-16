package pl.sgorski.AirLink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProfileResponse {

    @Schema(description = "User email", example = "test-email@yahoo.com")
    private String email;
    @Schema(description = "User first name", example = "John")
    private String firstName;
    @Schema(description = "User last name", example = "Doe")
    private String lastName;
    @Schema(description = "User phone number", example = "+1234567890")
    private String phoneNumber;
    @Schema(description = "User country", example = "USA")
    private String country;
    @Schema(description = "User zip code", example = "12345")
    private String zip;
    @Schema(description = "User city", example = "New York")
    private String city;
    @Schema(description = "User street", example = "123 Main St")
    private String street;
}
