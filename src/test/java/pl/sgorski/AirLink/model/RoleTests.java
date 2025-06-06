package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.Test;
import pl.sgorski.AirLink.model.auth.Role;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTests {

    @Test
    void shouldHaveAdminRole() {
        Role adminRole = new Role();
        adminRole.setName("admin");

        boolean result = adminRole.isAdmin();

        assertTrue(result);
    }

    @Test
    void shouldNotHaveAdminRole() {
        Role adminRole = new Role();
        adminRole.setName("user");

        boolean result = adminRole.isAdmin();

        assertFalse(result);
    }
}
