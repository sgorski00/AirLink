package pl.sgorski.AirLink.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Email is not valid")
    @Size(min = 5, max = 255, message = "Email must be between 5 and 255 characters")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
