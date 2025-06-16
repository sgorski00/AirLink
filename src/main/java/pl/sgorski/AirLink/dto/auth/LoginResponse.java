package pl.sgorski.AirLink.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT Token", example = "ifudhg983y985rh9g9842yut98hrg9uhfsg9u8h4g9uhf")
    private String token;
}
