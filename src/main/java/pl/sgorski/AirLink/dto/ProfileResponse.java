package pl.sgorski.AirLink.dto;

import lombok.Data;

@Data
public class ProfileResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String zip;
    private String city;
    private String street;
}
