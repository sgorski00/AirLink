package pl.sgorski.AirLink.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.sgorski.AirLink.dto.RegisterRequest;
import pl.sgorski.AirLink.dto.RegisterResponse;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegistrationMapperTests {

    private final RegistrationMapper mapper = Mappers.getMapper(RegistrationMapper.class);

    private final User user = new User();
    private final Role role = new Role();
    private final RegisterRequest request = new RegisterRequest();

    @BeforeEach
    void setUp() {
        user.setId(5L);
        user.setEmail("email@email.com");
        role.setId(1L);
        role.setName("USER");
        user.setRole(role);
        request.setEmail("email@email.com");
        request.setPassword("password");
    }

    @Test
    void shouldMapRegisterRequestToUser() {
        User user = mapper.toUser(request, role);

        assertEquals("email@email.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertNull(user.getId());
        assertEquals(role, user.getRole());
    }

    @Test
    void shouldMapUserToRegisterResponse() {
        RegisterResponse response = mapper.toResponse(user);

        assertEquals("email@email.com", response.getEmail());
        assertEquals("USER", response.getRole());
        assertEquals(5L, response.getId());
    }
}
