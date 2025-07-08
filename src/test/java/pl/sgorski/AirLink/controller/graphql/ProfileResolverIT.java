package pl.sgorski.AirLink.controller.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import pl.sgorski.AirLink.containers_config.BaseIT;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureGraphQlTester
public class ProfileResolverIT extends BaseIT {

    @Autowired
    private GraphQlTester tester;

    @Test
    @WithUserDetails("test@user.com")
    void shouldReturnProfile() {
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
                .path("profile.email").entity(String.class).isEqualTo("test@user.com")
                .path("profile.firstName").entity(String.class).isEqualTo("John")
                .path("profile.lastName").entity(String.class).isEqualTo("Doe");
    }

    @Test
    @WithUserDetails("test@user2.com")
    void shouldReturnEmptyProfile_NullValues() {
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
                .path("profile.email").entity(String.class).isEqualTo("test@user2.com")
                .path("profile.firstName").valueIsNull()
                .path("profile.lastName").valueIsNull();
    }

    @Test
    @WithAnonymousUser
    void shouldNotReturnProfile_Forbidden() {
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
                .errors()
                .satisfy(errors -> {
                    assertNotNull(errors);
                    assertEquals("Access Denied", errors.getFirst().getMessage());
                });
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldUpdateProfile() {
        String mutation = """
                    mutation UpdateProfile($profileInput: ProfileInput!) {
                        updateProfile(profileInput: $profileInput) {
                            email
                            firstName
                            lastName
                            phoneNumber
                            country
                            zip
                            city
                            street
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
                .path("updateProfile.email").entity(String.class).isEqualTo("test@user.com")
                .path("updateProfile.firstName").entity(String.class).isEqualTo("Jane")
                .path("updateProfile.lastName").entity(String.class).isEqualTo("Dane")
                .path("updateProfile.phoneNumber").entity(String.class).isEqualTo("123456789")
                .path("updateProfile.country").entity(String.class).isEqualTo("Wonderland")
                .path("updateProfile.zip").entity(String.class).isEqualTo("12345")
                .path("updateProfile.city").entity(String.class).isEqualTo("Wonder City")
                .path("updateProfile.street").entity(String.class).isEqualTo("Wonder Street");
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldNotUpdateProfile_NotValidData() {
        String mutation = """
                    mutation UpdateProfile($profileInput: ProfileInput!) {
                        updateProfile(profileInput: $profileInput) {
                            email
                            firstName
                            lastName
                        }
                    }
                """;

        Map<String, Object> request = new HashMap<>();
        request.put("firstName", "J");
        request.put("lastName", "Dane");
        request.put("phoneNumber", "123456789");
        request.put("country", "Wonderland");
        request.put("zip", "12345");
        request.put("city", "Wonder City");
        request.put("street", "Wonder Street");

        tester.document(mutation)
                .variable("profileInput", request)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertNotNull(errors);
                    assertNotNull(errors.getFirst().getMessage());
                });
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldNotUpdateProfile_MissingData() {
        String mutation = """
                    mutation UpdateProfile($profileInput: ProfileInput!) {
                        updateProfile(profileInput: $profileInput) {
                            email
                            firstName
                            lastName
                            phoneNumber
                            country
                            zip
                            city
                            street
                        }
                    }
                """;

        Map<String, Object> request = new HashMap<>();
        request.put("firstName", "Jane");
        request.put("lastName", "Dane");

        tester.document(mutation)
                .variable("profileInput", request)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertNotNull(errors);
                    assertNotNull(errors.getFirst().getMessage());
                });
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldNotUpdateProfile_TooLongValue() {
        String mutation = """
                    mutation UpdateProfile($profileInput: ProfileInput!) {
                        updateProfile(profileInput: $profileInput) {
                            email
                            firstName
                            lastName
                            phoneNumber
                            country
                            zip
                            city
                            street
                        }
                    }
                """;

        Map<String, Object> request = new HashMap<>();
        request.put("firstName", "a".repeat(51));
        request.put("lastName", "Dane");

        tester.document(mutation)
                .variable("profileInput", request)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertNotNull(errors);
                    assertNotNull(errors.getFirst().getMessage());
                });
    }

    @Test
    @WithUserDetails("test@user.com")
    void shouldClearProfile() {
        String mutation = """
                    mutation ClearProfile {
                        clearProfile {
                            email
                            firstName
                            lastName
                        }
                    }
                """;

        tester.document(mutation)
                .execute()
                .path("clearProfile.email").entity(String.class).isEqualTo("test@user.com")
                .path("clearProfile.firstName").valueIsNull()
                .path("clearProfile.lastName").valueIsNull();
    }
}
