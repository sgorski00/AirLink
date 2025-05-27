package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.dto.LoginRequest;
import pl.sgorski.AirLink.dto.LoginResponse;
import pl.sgorski.AirLink.dto.RegisterRequest;
import pl.sgorski.AirLink.dto.RegisterResponse;
import pl.sgorski.AirLink.mapper.RegistrationMapper;
import pl.sgorski.AirLink.model.Role;
import pl.sgorski.AirLink.model.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final RoleService roleService;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails user = userService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }

    public RegisterResponse register(RegisterRequest request) {
        Role defaultRole = roleService.findByName("USER");
        User user = registrationMapper.toUser(request, defaultRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.save(user);
        return registrationMapper.toResponse(savedUser);
    }
}
