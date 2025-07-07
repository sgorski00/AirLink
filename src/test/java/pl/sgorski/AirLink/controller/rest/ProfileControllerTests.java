package pl.sgorski.AirLink.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.AirLink.dto.ProfileResponse;
import pl.sgorski.AirLink.mapper.ProfileMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.service.ProfileService;
import pl.sgorski.AirLink.service.auth.JwtService;
import pl.sgorski.AirLink.service.auth.UserService;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProfileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private ProfileMapper profileMapper;

    @MockitoBean
    private JwtService jwtService;

    private User user;
    private ProfileResponse response;
    private Profile profile;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.pl");

        profile = new Profile();
        profile.setId(1L);
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setPhoneNumber("123456789");
        profile.setStreet("123 Main St");
        profile.setCity("Test City");
        profile.setZip("12345");
        profile.setCountry("Test Country");
        profile.setUser(user);
        user.setProfile(profile);

        response = new ProfileResponse();
        response.setCity(profile.getCity());
        response.setCountry(profile.getCountry());
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setStreet(profile.getStreet());
        response.setZip(profile.getZip());
        response.setEmail(user.getEmail());
    }

    @Test
    void shouldShowProfileDetails() throws Exception {
        when(profileService.getProfileByEmail(anyString())).thenReturn(profile);
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(response);
        mockMvc.perform(get("/api/profile")
                        .principal(() -> "test@test.pl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Profile retrieved successfully"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.firstName").value(profile.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(profile.getLastName()))
                .andExpect(jsonPath("$.data.phoneNumber").value(profile.getPhoneNumber()))
                .andExpect(jsonPath("$.data.street").value(profile.getStreet()))
                .andExpect(jsonPath("$.data.city").value(profile.getCity()))
                .andExpect(jsonPath("$.data.zip").value(profile.getZip()))
                .andExpect(jsonPath("$.data.country").value(profile.getCountry()))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()));
    }

    @Test
    void shouldNotShowProfileDetailsIfUserNotFound() throws Exception {
        when(profileService.getProfileByEmail(anyString())).thenThrow(new NoSuchElementException("User not found"));
        mockMvc.perform(get("/api/profile")
                        .principal(() -> "test@test.pl"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("User not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldUpdateProfile() throws Exception {
        when(profileService.getProfileByEmail(anyString())).thenReturn(profile);
        when(profileService.save(any(Profile.class))).thenReturn(profile);
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(response);

        mockMvc.perform(put("/api/profile")
                        .principal(() -> "test@test.pl")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new pl.sgorski.AirLink.dto.ProfileRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Profile updated successfully"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void shouldNotUpdateProfileIfUserNotFound() throws Exception {
        when(profileService.getProfileByEmail(anyString())).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put("/api/profile")
                        .principal(() -> "test@test.pl")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new pl.sgorski.AirLink.dto.ProfileRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("User not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldClearProfile() throws Exception {
        when(profileService.getProfileByEmail(anyString())).thenReturn(profile);
        when(profileService.clearProfile(any(Profile.class))).thenReturn(profile);
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(response);

        mockMvc.perform(put("/api/profile/clear")
                        .principal(() -> "test@test.pl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Profile cleared successfully"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void shouldNotClearProfileIfUserNotFound() throws Exception {
        when(profileService.getProfileByEmail(anyString())).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put("/api/profile/clear")
                        .principal(() -> "test@test.pl"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("User not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
