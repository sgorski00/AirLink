package pl.sgorski.AirLink.controller.graphql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.sgorski.AirLink.dto.ProfileResponse;
import pl.sgorski.AirLink.mapper.ProfileMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.service.ProfileService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@GraphQlTest(ProfileResolver.class)
@AutoConfigureGraphQlTester
public class ProfileResolverTests {

    @Autowired
    private GraphQlTester tester;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private ProfileMapper profileMapper;

    private ProfileResponse profileResponse;
    private final String userEmail = "test@email.com";

    @BeforeEach
    void setUp() {
        profileResponse = new ProfileResponse();
        profileResponse.setEmail(userEmail);
    }

    @Test
    @WithMockUser(username = userEmail)
    void shouldReturnProfile() {
        when(profileService.getProfileByEmail(anyString())).thenReturn(new Profile());
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(profileResponse);

        String query = """
                    query {
                        profile {
                            email
                            firstName
                            lastName
                        }
                    }
                """;

        tester.document(query)
                .execute()
                .path("profile.email").entity(String.class).isEqualTo(userEmail)
                .path("profile.firstName").valueIsNull()
                .path("profile.lastName").valueIsNull();

        verify(profileService, times(1)).getProfileByEmail(anyString());
    }

    @Test
    @WithMockUser(username = userEmail)
    void shouldUpdateProfile() {
        when(profileService.getProfileByEmail(anyString())).thenReturn(new Profile());
        when(profileService.save(any(Profile.class))).thenReturn(new Profile());
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(profileResponse);

        String mutation = """
                    mutation UpdateProfile($profileInput: ProfileInput!) {
                        updateProfile(profileInput: $profileInput) {
                            email
                        }
                    }
                """;

        Map<String, Object> request = new HashMap<>();
        request.put("firstName", "Jane");
        request.put("lastName", "Dane");
        request.put("phoneNumber", "123456789");
        request.put("country", "Wonderland");
        request.put("zip", "12345");
        request.put("city", "Wonder City");
        request.put("street", "Wonder Street");

        tester.document(mutation)
                .variable("profileInput", request)
                .execute()
                .path("updateProfile.email").entity(String.class).isEqualTo(userEmail);

        verify(profileService, times(1)).save(any(Profile.class));
        verify(profileMapper, times(1)).updateProfile(any(), any(Profile.class));
    }

    @Test
    @WithMockUser(username = userEmail)
    void shouldClearProfile() {
        when(profileService.getProfileByEmail(anyString())).thenReturn(new Profile());
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(profileResponse);

        String mutation = """
                    mutation ClearProfile {
                        clearProfile {
                            email
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .path("clearProfile.email").entity(String.class).isEqualTo(userEmail);

        verify(profileService, times(1)).clearProfile(any(Profile.class));
        verify(profileService, times(1)).getProfileByEmail(anyString());
    }
}
