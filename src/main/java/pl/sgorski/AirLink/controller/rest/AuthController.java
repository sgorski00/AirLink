package pl.sgorski.AirLink.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.AirLink.dto.auth.LoginRequest;
import pl.sgorski.AirLink.dto.auth.LoginResponse;
import pl.sgorski.AirLink.dto.auth.RegisterRequest;
import pl.sgorski.AirLink.dto.auth.RegisterResponse;
import pl.sgorski.AirLink.dto.generic.ResponseDto;
import pl.sgorski.AirLink.service.auth.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate a user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var response = new ResponseDto<>("User logged in successfully", 200, authenticationService.authenticate(request));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "409", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        int code = 201;
        var response = new ResponseDto<>("User registered successfully", code, authenticationService.register(request));
        return ResponseEntity.status(code).body(response);
    }
}