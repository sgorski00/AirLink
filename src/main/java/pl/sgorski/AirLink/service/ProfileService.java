package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.repository.ProfileRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Profile getProfileByEmail(String email) {
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Profile not found for email: " + email));
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile clearProfile(Profile profile) {
        profile.clear();
        return save(profile);
    }
}
