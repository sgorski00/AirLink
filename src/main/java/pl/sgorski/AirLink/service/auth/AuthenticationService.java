package pl.sgorski.AirLink.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.dto.LoginRequest;
import pl.sgorski.AirLink.dto.LoginResponse;
import pl.sgorski.AirLink.dto.RegisterRequest;
import pl.sgorski.AirLink.dto.RegisterResponse;
import pl.sgorski.AirLink.mapper.RegistrationMapper;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final RoleService roleService;
    private final RegistrationMapper registrationMapper;

    public LoginResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }

    public RegisterResponse register(RegisterRequest request) {
        Role defaultRole = roleService.findByName("USER");
        User user = registrationMapper.toUser(request, defaultRole);
        User savedUser = userService.save(user);
        return registrationMapper.toResponse(savedUser);
    }
}
