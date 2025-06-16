package pl.sgorski.AirLink.dto.auth;

import lombok.Data;

@Data
public class RegisterResponse {
    private Long id;
    private String email;
    private String role;
}
