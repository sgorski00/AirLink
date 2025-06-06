package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.Test;
import pl.sgorski.AirLink.model.auth.User;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProfileTests {

    @Test
    void shouldClearProfileData() {
        User user = new User();
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setId(1L);
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setPhoneNumber("123456789");
        profile.setCountry("Country");
        profile.setZip("12345");
        profile.setCity("City");
        profile.setStreet("Street");

        profile.clear();

        assertNotNull(profile.getUser());
        assertNotNull(profile.getId());
        assertNull(profile.getFirstName());
        assertNull(profile.getLastName());
        assertNull(profile.getPhoneNumber());
        assertNull(profile.getCountry());
        assertNull(profile.getZip());
        assertNull(profile.getCity());
        assertNull(profile.getStreet());
    }
}
