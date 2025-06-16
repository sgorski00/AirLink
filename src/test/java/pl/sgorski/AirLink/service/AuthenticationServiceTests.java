package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.sgorski.AirLink.dto.auth.LoginRequest;
import pl.sgorski.AirLink.dto.auth.LoginResponse;
import pl.sgorski.AirLink.dto.auth.RegisterRequest;
import pl.sgorski.AirLink.dto.auth.RegisterResponse;
import pl.sgorski.AirLink.mapper.RegistrationMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.service.auth.AuthenticationService;
import pl.sgorski.AirLink.service.auth.JwtService;
import pl.sgorski.AirLink.service.auth.RoleService;
import pl.sgorski.AirLink.service.auth.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {

    @Mock
    private AuthenticationManager manager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private ProfileService profileService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RegistrationMapper mapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    private final LoginRequest loginRequest = new LoginRequest();
    private final RegisterRequest registerRequest = new RegisterRequest();
    private final User user = new User();
    private final Role role = new Role();

    @BeforeEach
    void setUp() {
        role.setName("USER");
        loginRequest.setEmail("email@email.com");
        loginRequest.setPassword("password");
        registerRequest.setEmail("email@email.com");
        registerRequest.setPassword("password");
    }

    @Test
    void shouldAuthenticateCorrectRequest() {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        LoginResponse response = authenticationService.authenticate(loginRequest);

        assertNotNull(response.getToken());
        assertEquals("token", response.getToken());
    }

    @Test
    void shouldThrowExceptionWhileAuthenticationIfManagerThrow() {
        when(manager.authenticate(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginRequest));
        verify(manager, times(1)).authenticate(any());
        verify(userDetailsService, times(0)).loadUserByUsername(anyString());
        verify(jwtService, times(0)).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhileAuthenticationIfUserNotFound() {
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginRequest));
        verify(manager, times(1)).authenticate(any());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(jwtService, times(0)).generateToken(any(User.class));
    }

    @Test
    void shouldRegisterUser() {
        Profile profile = new Profile();
        user.setPassword("pass");
        when(profileService.save(any(Profile.class))).thenReturn(profile);
        when(roleService.findByName(anyString())).thenReturn(role);
        when(mapper.toUser(any(RegisterRequest.class), any(Role.class))).thenReturn(user);
        when(mapper.toResponse(any(User.class))).thenReturn(new RegisterResponse());
        when(userService.save(any(User.class))).thenReturn(user);

        RegisterResponse response = authenticationService.register(registerRequest);

        assertNotNull(response);
        verify(userService, times(1)).save(any(User.class));
        verify(profileService, times(1)).save(any(Profile.class));
    }
}
