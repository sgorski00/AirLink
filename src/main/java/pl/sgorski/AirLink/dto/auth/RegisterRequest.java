package pl.sgorski.AirLink.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pl.sgorski.AirLink.validator.Password;

@Data
public class RegisterRequest {

    @Email(message = "Email is not valid")
    @Size(min = 5, max = 255, message = "Email must be between 5 and 255 characters")
    private String email;

    @Password(message = "Password must contain at least one letter, one number, and should be between 8 and 50 characters")
    private String password;
}
