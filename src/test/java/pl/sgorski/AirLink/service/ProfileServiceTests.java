package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.repository.ProfileRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTests {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void shouldSaveProfile() {
        Profile profile = new Profile();

        profileService.save(profile);

        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void shouldReturnProfileByEmail() {
        when(profileRepository.findByEmail(anyString())).thenReturn(Optional.of(new Profile()));

        Profile result = profileService.getProfileByEmail("test");

        assertNotNull(result);
        verify(profileRepository, times(1)).findByEmail("test");
    }

    @Test
    void shouldThrowWhenProfileNotFoundByEmail() {
        when(profileRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> profileService.getProfileByEmail("test"));

        verify(profileRepository, times(1)).findByEmail("test");
    }

    @Test
    void shouldClearProfile() {
        Profile profile = new Profile();
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile result = profileService.clearProfile(profile);

        assertNotNull(result);
        verify(profileRepository, times(1)).save(profile);
    }
}
