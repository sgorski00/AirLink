package pl.sgorski.AirLink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.AirLink.dto.LoginRequest;
import pl.sgorski.AirLink.dto.LoginResponse;
import pl.sgorski.AirLink.dto.RegisterRequest;
import pl.sgorski.AirLink.dto.RegisterResponse;
import pl.sgorski.AirLink.service.auth.AuthenticationService;
import pl.sgorski.AirLink.service.auth.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private final String validationErrorMsg = "Password must contain at least one letter, one number, and should be between 8 and 50 characters";

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("user@email.com");
        loginRequest.setPassword("password1");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("user@email.com");
        registerRequest.setPassword("password1");
    }

    @Test
    void shouldLogin() throws Exception {
        LoginResponse response = new LoginResponse("token");

        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("User logged in successfully"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.token").value("token"));
    }

    @Test
    void shouldNotLoginWhenUserNotFound() throws Exception {
        when(authenticationService.authenticate(any(LoginRequest.class))).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("User not found"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldNotLoginWhenCredentialsAreWrong() throws Exception {
        when(authenticationService.authenticate(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Bad credentials"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldRegister() throws Exception {
        RegisterResponse response = new RegisterResponse();
        response.setEmail("user@email.com");
        response.setRole("USER");
        response.setId(999L);
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detail").value("User registered successfully"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.email").value("user@email.com"))
                .andExpect(jsonPath("$.data.roleName").value("USER"))
                .andExpect(jsonPath("$.data.id").value(999L));
    }

    @Test
    void shouldNotRegisterIfEmailIsWrong() throws Exception {
        registerRequest.setEmail("wrong-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Email is not valid"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotRegisterIfEmailHasWhitespaces() throws Exception {
        registerRequest.setEmail("   email@email.com    ");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Email is not valid"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotRegisterIfPasswordIsBlank() throws Exception {
        registerRequest.setPassword("        ");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(validationErrorMsg))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotRegisterIfPasswordHasWhitespaces() throws Exception {
        registerRequest.setPassword("Strong password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(validationErrorMsg))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotRegisterIfPasswordIsTooShort() throws Exception {
        registerRequest.setPassword("hey123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(validationErrorMsg))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotRegisterIfPasswordIsTooLong() throws Exception {
        registerRequest.setPassword("a1".repeat(30));

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(validationErrorMsg))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotRegisterIfPasswordDoesNotContainLetter() throws Exception {
        registerRequest.setPassword("12345678");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(validationErrorMsg))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldNotRegisterIfPasswordDoesNotContainNumber() throws Exception {
        registerRequest.setPassword("aAaAaAaA");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(validationErrorMsg))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldRegisterIfPasswordContainsSymbols() throws Exception {
        RegisterResponse response = new RegisterResponse();
        response.setEmail("user@email.com");
        response.setRole("USER");
        response.setId(999L);
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        registerRequest.setPassword("FhN&(=yO_bbpGQ4&m[");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detail").value("User registered successfully"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.email").value("user@email.com"))
                .andExpect(jsonPath("$.data.roleName").value("USER"))
                .andExpect(jsonPath("$.data.id").value(999L));
    }
}
