package pl.sgorski.AirLink.controller.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.sgorski.AirLink.dto.ProfileResponse;
import pl.sgorski.AirLink.mapper.ProfileMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.service.ProfileService;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class ProfileResolverTests {

    @Autowired
    private GraphQlTester tester;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private ProfileMapper profileMapper;

    @Test
    @WithMockUser(username = "test@email.com")
    void shouldReturnProfile() {
        User user = new User();
        user.setEmail("test@email.com");
        Profile profile = new Profile();
        profile.setUser(user);
        ProfileResponse response = new ProfileResponse();
        response.setEmail("test@email.com");

        when(profileService.getProfileByEmail("test@email.com")).thenReturn(profile);
        when(profileMapper.toResponse(profile)).thenReturn(response);

        String query = """
                    query {
                        profile {
                            email
                        }
                    }
                """;

        tester.document(query)
                .execute()
                .path("profile.email")
                .entity(String.class)
                .isEqualTo("test@email.com");
    }
}
