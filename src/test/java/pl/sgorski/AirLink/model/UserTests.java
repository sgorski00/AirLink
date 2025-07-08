package pl.sgorski.AirLink.model;

import org.junit.jupiter.api.Test;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    void shouldReturnTrueIfHaveAccessToOwnable_CorrectId() {
        Role role = new Role();
        role.setName("USER");
        User user = new User();
        user.setRole(role);
        user.setId(1L);
        Reservation reservation = new Reservation();
        reservation.setUser(user);

        boolean hasAccess = user.haveAccess(reservation);

        assertTrue(hasAccess);
    }

    @Test
    void shouldReturnTrueIfHaveAccessToOwnable_Admin() {
        Role role = new Role();
        role.setName("ADMIN");
        User admin = new User();
        admin.setRole(role);
        admin.setId(999L);
        User user = new User();
        user.setId(1L);
        Reservation reservation = new Reservation();
        reservation.setUser(user);

        boolean hasAccess = admin.haveAccess(reservation);

        assertTrue(hasAccess);
    }

    @Test
    void shouldReturnFalseIfDoNotHaveAccessToOwnable() {
        Role role = new Role();
        role.setName("USER");
        User owner = new User();
        owner.setId(999L);
        User user = new User();
        user.setId(1L);
        user.setRole(role);
        Reservation reservation = new Reservation();
        reservation.setUser(owner);

        boolean hasAccess = user.haveAccess(reservation);

        assertFalse(hasAccess);
    }

    @Test
    void shouldReturnFalseIfOwnableObjectIsNull() {
        Role role = new Role();
        role.setName("ADMIN");
        User user = new User();
        user.setId(1L);
        user.setRole(role);

        boolean hasAccess = user.haveAccess(null);

        assertFalse(hasAccess);
    }

    @Test
    void shouldReturnRoleAsAuthorities() {
        Role role = new Role();
        role.setName("USER");
        User user = new User();
        user.setRole(role);

        var result = user.getAuthorities();

        assertTrue(result.contains(role));
        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmailAsUsername() {
        User user = new User();
        user.setEmail("test@email.com");

        String result = user.getUsername();

        assertEquals("test@email.com", result);
    }
}
