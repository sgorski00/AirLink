package pl.sgorski.AirLink.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.AirLink.dto.ResponseDto;
import pl.sgorski.AirLink.dto.ProfileRequest;
import pl.sgorski.AirLink.mapper.ProfileMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.service.ProfileService;
import pl.sgorski.AirLink.service.auth.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    @GetMapping
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
