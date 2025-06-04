package pl.sgorski.AirLink.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(min = 5, max = 15, message = "Phone number must be between 5 and 15 characters")
    private String phoneNumber;

    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    private String country;

    @Size(min = 2, max = 20, message = "Zip code must be between 2 and 20 characters")
    private String zip;

    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

    @Size(min = 2, max = 100, message = "Street must be between 2 and 100 characters")
    private String street;
}
