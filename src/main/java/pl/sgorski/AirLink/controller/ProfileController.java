package pl.sgorski.AirLink.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.ProfileResponse;
import pl.sgorski.AirLink.dto.generic.ResponseDto;
import pl.sgorski.AirLink.dto.ProfileRequest;
import pl.sgorski.AirLink.mapper.ProfileMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.service.ProfileService;
import pl.sgorski.AirLink.service.auth.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Endpoints for managing your profile")
public class ProfileController {

    private final UserService userService;
    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Get user profile", description = "Retrieve the profile of the authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(schema = @Schema(implementation = ProfileResponse.class)))
    public ResponseEntity<?> getProfile(
            Principal principal
    ) {
        Profile profile = userService.findByEmail(principal.getName()).getProfile();
        return ResponseEntity.ok(new ResponseDto<>(
                "Profile retrieved successfully",
                200,
                profileMapper.toResponse(profile))
        );
    }

    @PutMapping
    @Operation(summary = "Update user profile", description = "Update your profile information")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(schema = @Schema(implementation = ProfileResponse.class)))
    public ResponseEntity<?> updateProfile(
            Principal principal,
            @Valid @RequestBody ProfileRequest request
    ) {
        Profile existingProfile = userService.findByEmail(principal.getName()).getProfile();
        profileMapper.updateProfile(request, existingProfile);
        Profile profile = profileService.save(existingProfile);
        return ResponseEntity.ok(new ResponseDto<>(
                "Profile updated successfully",
                200,
                profileMapper.toResponse(profile))
        );
    }

    @PutMapping("/clear")
    @Operation(summary = "Clear user profile", description = "Clear all data from your profile")
    @ApiResponse(responseCode = "200", description = "Profile cleared successfully", content = @Content(schema = @Schema(implementation = ProfileResponse.class)))
    public ResponseEntity<?> clearProfile(
            Principal principal
    ) {
        Profile profile = userService.findByEmail(principal.getName()).getProfile();
        profile.clear();
        profile = profileService.save(profile);
        return ResponseEntity.ok(new ResponseDto<>(
                "Profile cleared successfully",
                200,
                profileMapper.toResponse(profile))
        );
    }
}
