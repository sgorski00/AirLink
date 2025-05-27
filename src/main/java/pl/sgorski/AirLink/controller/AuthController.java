package pl.sgorski.AirLink.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.AirLink.dto.*;
import pl.sgorski.AirLink.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        var response = new ApiResponse<>("User logged in successfully", 200, authenticationService.authenticate(request));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        int code = 201;
        var response = new ApiResponse<>("User registered successfully", code, authenticationService.register(request));
        return ResponseEntity.status(code).body(response);
    }
}