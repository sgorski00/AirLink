package pl.sgorski.AirLink.controller.graphql;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sgorski.AirLink.dto.ProfileRequest;
import pl.sgorski.AirLink.dto.ProfileResponse;
import pl.sgorski.AirLink.mapper.ProfileMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.service.ProfileService;

import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ProfileResolver {

    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    @QueryMapping("profile")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponse getProfile(
            Principal principal
    ) {
        Profile profile =  profileService.getProfileByEmail(principal.getName());
        return profileMapper.toResponse(profile);
    }

    @MutationMapping("updateProfile")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponse updateProfile(
            Principal principal,
            @Argument("profileInput") @Valid ProfileRequest request
    ) {
        Profile existingProfile = profileService.getProfileByEmail(principal.getName());
        profileMapper.updateProfile(request, existingProfile);
        Profile profile = profileService.save(existingProfile);
        return profileMapper.toResponse(profile);
    }

    @MutationMapping("clearProfile")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponse clearProfile(
            Principal principal
    ) {
        Profile profile = profileService.getProfileByEmail(principal.getName());
        profileService.clearProfile(profile);
        return profileMapper.toResponse(profile);
    }
}
