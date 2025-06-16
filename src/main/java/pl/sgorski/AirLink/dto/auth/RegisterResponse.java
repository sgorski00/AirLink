package pl.sgorski.AirLink.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterResponse {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "User email", example = "some-email@yahoo.com")
    private String email;
    @Schema(description = "User role", example = "USER")
    private String role;
}
